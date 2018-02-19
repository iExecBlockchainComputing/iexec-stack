#!/bin/bash
#
# File : /Applications/xwhep.worker/bin/uninstall.sh
#
# Copyrights     : CNRS
# Author         : Oleg Lodygensky
# Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
# Web            : http://www.xtremweb-hep.org
# 
#      This file is part of XtremWeb-HEP.
#
#    XtremWeb-HEP is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    XtremWeb-HEP is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
#

SYSLOGIN="@SYSTEMUSER@"
PKG="xwhep.worker"
PROG="xtremweb.worker"
# this is the name defined in /Library/LaunchDaemons/xwhep.worker.plist
LAUNCHNAME="fr.in2p3.lal.xwhep.worker"

USRBIN=/usr/bin
USRBINXWCREATEVDI=$USRBIN/xwcreatevdi
USRLOCALBIN=/usr/local/bin
CREATEVDI=$USRLOCALBIN/createvdi
CREATEISO=$USRLOCALBIN/createiso

HOST=`uname -a | cut -d ' ' -f 2`
LOGFILENAME=$PROG-$HOST.log
LOGFILE=/var/log/$LOGFILENAME
touch $LOGFILE

echo "*****************************" >> $LOGFILE 2>&1
echo "* [`date`] [$PROG] INFO : uninstalling $PROG"  >> $LOGFILE 2>&1


# next are  needed because previous versions used different directory
if [ -d /Library/StartupItems/$PROG ] ; then
    PKG=$PROG
fi

sudo /private/etc/$PKG/bin/rmuser.sh xtremweb >> $LOGFILE 2>&1
sudo /private/etc/$PKG/bin/rmuser.sh xtremwebwrk >> $LOGFILE 2>&1
sudo /private/etc/$PKG/bin/rmuser.sh xtremwebsrv >> $LOGFILE 2>&1


# current version

# Mac OS prior to 10.4
if [ -d /Library/StartupItems/$PKG ] ; then
    /Library/StartupItems/$PKG/$PKG stop >> $LOGFILE 2>&1
fi

# Mac OS since 10.4
if [ -f /Library/LaunchDaemons/$LAUNCHNAME.plist >> $LOGFILE 2>&1 ] ; then
    launchctl unload /Library/LaunchDaemons/$LAUNCHNAME.plist >> $LOGFILE 2>&1
fi

# since 12.2.8, we create as many users as available CPU in a single group
# see xtremweb.common.XWPropertyDefs#OSACCOUNT
# see xtremweb.Woker.ThreadLaunch#getNextOsAccount()
# see xtremweb.Woker.ThreaWork#getBinPath()

nbCpu=$(system_profiler SPHardwareDataType | grep 'Cores:' | cut -d ':' -f 2)
nbCpu=$(( nbCpu - 1 ))
[ ${nbCpu} -lt 1 ] && nbCpu=1
i=0
while [ ${i} -lt ${nbCpu} ] ; do
    USERLOGIN=${SYSLOGIN}${i}
    sudo ${BINDIR}/rmuser.sh ${USERLOGIN} >> ${LOGFILE} 2>&1
done

sudo /private/etc/$PKG/bin/rmuser.sh $SYSLOGIN >> $LOGFILE 2>&1

sudo rm -Rf /private/etc/$PKG >> $LOGFILE 2>&1
sudo rm -Rf /usr/local/bin/$PROG >> $LOGFILE 2>&1
sudo rm -Rf /Library/StartupItems/$PKG >> $LOGFILE 2>&1
# next was the old file name
sudo rm -f  /Library/LaunchDaemons/$PKG.plist >> $LOGFILE 2>&1
# next is the current file name
sudo rm -f  /Library/LaunchDaemons/$LAUNCHNAME.plist >> $LOGFILE 2>&1
sudo rm -Rf  /Applications/$PKG >> $LOGFILE 2>&1
sudo rm -f $CREATEVDI >> $LOGFILE  2>&1 
sudo rm -f $CREATEISO >> $LOGFILE  2>&1 
sudo rm -f $USRBINXWCREATEVDI >> $LOGFILE  2>&1 

echo "* [`date`] [$PROG] INFO : $PROG successfully uninstalled"  >> $LOGFILE 2>&1
echo "*****************************" >> $LOGFILE 2>&1

#
# EOF
#
