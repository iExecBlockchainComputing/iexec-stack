#!/bin/bash
#
# File : xwhep.server/installer/PckRoot/Applications/xwhep.server/bin/start.sh
#


PKG="xwhep.server"
PROG="xtremweb.server"
# this is the name defined in /Library/LaunchDaemons/xwhep.server.plist
LAUNCHNAME="fr.in2p3.lal.xwhep.server"
SYSLOGIN="@SYSTEMUSER@"

HOST=`uname -a | cut -d ' ' -f 2`
LOGFILE=/var/log/$PROG-$HOST.log
touch $LOGFILE

echo "*****************************" >> $LOGFILE 2>&1
echo "* [`date`] [$PROG] INFO : starting $PROG"  >> $LOGFILE 2>&1

echo "chown $SYSLOGIN:$SYSLOGIN $LOGFILE" >> $LOGFILE 2>&1
chown $SYSLOGIN:$SYSLOGIN $LOGFILE >> $LOGFILE 2>&1

STARTED="OK"

#
# we don't use Mac OS specific stuffs
#
launchctl load -w /Library/LaunchDaemons/$LAUNCHNAME.plist >> $LOGFILE 2>&1
#/usr/local/bin/$PROG start
if [ $? -ne 0 ] ; then
    STARTED="FAILED"
fi

echo "`date` [`uname -n` $PROG] INFO : $PROG start [$STARTED] "

echo "* [`date`] [$PROG] INFO : $PROG started [$STARTED]" >> $LOGFILE 2>&1
echo "*****************************" >> $LOGFILE 2>&1



#
# EOF
#
