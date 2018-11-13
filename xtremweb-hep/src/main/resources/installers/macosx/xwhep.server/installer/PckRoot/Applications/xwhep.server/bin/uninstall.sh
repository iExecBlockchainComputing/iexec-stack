#!/bin/bash
#
# File : xwhep.server/uninstaller/Ressources/postflight
#

SYSLOGIN="@SYSTEMUSER@"
PKG="xwhep.server"
PROG="xtremweb.server"
# this is the name defined in /Library/LaunchDaemons/xwhep.server.plist
LAUNCHNAME="fr.in2p3.lal.xwhep.server"

HOST=`uname -a | cut -d ' ' -f 2`
LOGFILE=/var/log/$PROG-$HOST.log
touch $LOGFILE

echo "*****************************" >> $LOGFILE 2>&1
echo "* [`date`] [$PROG] INFO : uninstalling $PROG"  >> $LOGFILE 2>&1


# next are  needed because previous versions used different directory
if [ -d /Library/StartupItems/$PROG ] ; then
    PKG=$PROG
fi


# Mac OS prior to 10.4
if [ -d /Library/StartupItems/$PKG ] ; then
    /Library/StartupItems/$PKG/$PKG stop >> $LOGFILE 2>&1
fi

# Mac OS since 10.4
if [ -f /Library/LaunchDaemons/$LAUNCHNAME.plist ] ; then
    launchctl unload /Library/LaunchDaemons/$LAUNCHNAME.plist >> $LOGFILE 2>&1
fi

#
# we don't use Mac OS specific stuffs
#
#/usr/local/bin/$PROG stop


sudo /private/etc/$PKG/bin/rmuser.sh xtremweb >> $LOGFILE 2>&1
sudo /private/etc/$PKG/bin/rmuser.sh xtremwebwrk >> $LOGFILE 2>&1
sudo /private/etc/$PKG/bin/rmuser.sh xtremwebsrv >> $LOGFILE 2>&1


sudo /private/etc/$PKG/bin/rmuser.sh $SYSLOGIN >> $LOGFILE 2>&1

rm -Rf /private/etc/$PKG >> $LOGFILE 2>&1
rm -Rf /usr/local/bin/$PROG >> $LOGFILE 2>&1
rm -Rf /Library/StartupItems/$PKG >> $LOGFILE 2>&1
rm -f  /Library/LaunchDaemons/$LAUNCHNAME.plist >> $LOGFILE 2>&1
rm -Rf  /Applications/$PKG >> $LOGFILE 2>&1

echo "* [`date`] [$PROG] INFO : $PROG successfully uninstalled"  >> $LOGFILE 2>&1
echo "*****************************" >> $LOGFILE 2>&1

#
# EOF
#
