#!/bin/bash
#
# File : xwhep.worker/installer/PckRoot/Applications/xwhep.worker/bin/start.sh
#
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

ERROK=0
ERRUSAGE=1
ERRFILE=2
ERRCONNECTION=3
ERRSTATE=4
ERRUSER=5

SYSLOGIN="@SYSTEMUSER@"
PKG="xwhep.worker"
PROG="xtremweb.worker"
# this is the name defined in /Library/LaunchDaemons/xwhep.worker.plist
LAUNCHNAME="fr.in2p3.lal.xwhep.worker"
VARXWHEP="/var/xwhep/worker"

HOST=`uname -a | cut -d ' ' -f 2`
LOGFILENAME=$PROG-$HOST.log
LOGFILE=/var/log/$LOGFILENAME
touch $LOGFILE

echo "*****************************" >> $LOGFILE 2>&1
echo "* [`date`] [$PROG] INFO : starting $PROG"  >> $LOGFILE 2>&1
echo "chown $SYSLOGIN:$SYSLOGIN $LOGFILE" >> $LOGFILE 2>&1
chown $SYSLOGIN:$SYSLOGIN $LOGFILE >> $LOGFILE 2>&1

STARTED="OK"
launchctl load -w /Library/LaunchDaemons/$LAUNCHNAME.plist >> $LOGFILE 2>&1
if [ $? -ne 0 ] ; then
    STARTED="FAILED"
fi

echo "`date` [`uname -n` $PROG] INFO : $PROG start [$STARTED] "

echo "* [`date`] [$PROG] INFO : $PROG started [$STARTED] " >> $LOGFILE 2>&1
echo "*****************************" >> $LOGFILE 2>&1


#return $ERROK

#
# EOF
#
