#!/bin/bash
#
# File : xwhep.server/uninstaller/PckRoot/Applications/xwhep.server/bin/stop.sh
#

PKG="xwhep.server"
PROG="xtremweb.server"
# this is the name defined in /Library/LaunchDaemons/xwhep.server.plist
LAUNCHNAME="fr.in2p3.lal.xwhep.server"

HOST=`uname -a | cut -d ' ' -f 2`
LOGFILE=/var/log/$PROG-$HOST.log
touch $LOGFILE

echo "*****************************" >> $LOGFILE 2>&1
echo "* [`date`] [$PROG] INFO : stopping $PROG"  >> $LOGFILE 2>&1

STOPED="OK"
#
# we don't use Mac OS specific stuffs
#
launchctl unload /Library/LaunchDaemons/$LAUNCHNAME.plist >> $LOGFILE 2>&1
#/usr/local/bin/$PROG stop
if [ $? -ne 0 ] ; then
    STOPED="FAILED"
fi

echo "`date` [`uname -n` $PROG] INFO : $PROG stop [$STOPED] "

echo "* [`date`] [$PROG] INFO : $PROG stop [$STOPED]"  >> $LOGFILE 2>&1
echo "*****************************" >> $LOGFILE 2>&1

#
# EOF
#
