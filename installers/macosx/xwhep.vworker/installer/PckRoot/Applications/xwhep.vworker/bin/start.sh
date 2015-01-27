#!/bin/bash

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
# File : xwhep.vworker/installer/PckRoot/Applications/xwhep.vworker/bin/start.sh
#

ERROK=0
ERRUSAGE=1
ERRFILE=2
ERRCONNECTION=3
ERRSTATE=4
ERRUSER=5

PKG="xwhep.vworker"

#
# Next variable is automatically set by Resources/postflight
#
VMNAME="vworker."$THEDATE


# this is the name defined in /Library/LaunchDaemons/xwhep.vworker.plist
LAUNCHNAME="fr.in2p3.lal.xwhep.$VMNAME"


VARXWHEP="/var/xwhep/vworker/$VMNAME"
LOGFILE="/var/log/$PKG_$VMNAME.log"
sudo touch $LOGFILE

echo "*****************************" >> $LOGFILE 2>&1
echo "* [`date`] [$PKG] INFO : starting $PKG"  >> $LOGFILE 2>&1

STARTED="OK"
sudo launchctl load -w /Library/LaunchDaemons/$LAUNCHNAME.plist >> $LOGFILE 2>&1
if [ $? -ne 0 ] ; then
    STARTED="FAILED"
fi

echo "`date` [`uname -n` $PKG] INFO : $PKG start [$STARTED] "

echo "* [`date`] [$PKG] INFO : $PKG started [$STARTED] " >> $LOGFILE 2>&1
echo "*****************************" >> $LOGFILE 2>&1


#return $ERROK

#
# EOF
#
