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
# File : /Applications/xwhep.vworker/bin/uninstall.sh
#

PKG="xwhep.vworker"

#
# Next variable is automatically set by Resources/postflight
#
VMNAME="vworker."$THEDATE


# this is the name defined in /Library/LaunchDaemons/xwhep.vworker.plist
THISLAUNCHNAME="fr.in2p3.lal.xwhep.$VMNAME"

LOGFILE="/var/log/"$PKG"_"$VMNAME".log"
sudo touch $LOGFILE

echo "*****************************" >> $LOGFILE 2>&1
echo "* [`date`] [$PKG] INFO : uninstalling $PKG"  >> $LOGFILE 2>&1


#sudo /private/etc/$PKG/bin/rmuser.sh xwhep >> $LOGFILE 2>&1


echo launchctl unload /Library/LaunchDaemons/$THISLAUNCHNAME.plist >> $LOGFILE 2>&1
sudo launchctl unload /Library/LaunchDaemons/$THISLAUNCHNAME.plist >> $LOGFILE 2>&1

echo "* [`date`] [$PKG] INFO : Unregistering $VMNAME" >> $LOGFILE 2>&1
/private/etc/$PKG/bin/xwstartvm.sh --uninstall --name $VMNAME >> $LOGFILE 2>&1


rm -f /private/etc/$PKG/iso/*$VMNAME* >> $LOGFILE 2>&1
rm -f /private/etc/$PKG/vdi/*$VMNAME* >> $LOGFILE 2>&1
rm -f /private/etc/$PKG/bin/*$VMNAME.sh >> $LOGFILE 2>&1
rm -Rf /private/etc/$PKG/bin/$VMNAME >> $LOGFILE 2>&1
rm -f  /Library/LaunchDaemons/*$VMNAME* >> $LOGFILE 2>&1
rm -f /Applications/xwhep.vworker/bin/start-$VMNAME.sh 
rm -f /Applications/xwhep.vworker/bin/stop-$VMNAME.sh 
rm -f /Applications/xwhep.vworker/bin/uninstall-$VMNAME.sh 

RMV=/Applications/$PKG/removed.txt
sudo touch  $RMV >> $LOGFILE 2>&1
echo "* [`date`] [$PKG] INFO : $VMNAME successfully removed"  >> $RMV 2>&1

echo "* [`date`] [$PKG] INFO : $PKG successfully uninstalled"  >> $LOGFILE 2>&1
echo "*****************************" >> $LOGFILE 2>&1

#
# EOF
#
