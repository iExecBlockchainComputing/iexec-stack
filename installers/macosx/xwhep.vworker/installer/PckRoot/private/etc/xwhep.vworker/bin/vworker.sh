#!/bin/sh


# Copyrights     : CNRS
# Author         : Oleg Lodygensky
# Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by INRIA : http://www.xtremweb.net/
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
#



#
# Date : Dec 15th, 2011
# File : vworker.sh
# Author : Oleg Lodygensky (lodygens A_T lal D_O_T IN2P3 D_O_T fr)
# Purpose: this launches an XWHEP worker in VirtualBox
#


#
# Next variable is automatically set by Resources/postflight
#
VMNAME="vworker."$THEDATE


PKG="xwhep.vworker"

LOGFILE="/var/log/$PKG"_"$VMNAME.log"
sudo touch $LOGFILE


THISLIVECD=`ls /private/etc/$PKG/iso/*$VMNAME.iso`
if [ ! -r $THISLIVECD ] ; then
    echo "* [`date`] [$PKG] ERROR : can't find $THISLIVECD" >> $LOGFILE 2>&1
    exit $ERRFILE
fi


THISCONTEXT=`ls /private/etc/$PKG/vdi/*$VMNAME.vdi`

CONTEXTUALIZATION=""
if [ -r $THISCONTEXT ] ; then
	CONTEXTUALIZATION="--context $THISCONTEXT"
fi

#
# a random SSHPORT
#
RANDOM=$$$(date %+s)
SSHPORT=`expr $RANDOM % 2048 + 2048`



PKG="xwhep.vworker"

# this waits to VB kernel extensiosn to be loaded
VBOXDRV=VBoxDrv
RC=1
while [ $RC -eq 1 ] ; do
    echo "Testing ${VBOXDRV}.kext" >> $LOGFILE 2>&1
    kextstat -lb org.virtualbox.kext.${VBOXDRV} 2>&1 | grep -q org.virtualbox.kext.${VBOXDRV}
    RC=$?
    if [ $RC -eq 1 ] ; then 
        echo "* [`date`] [$PKG] WARN : ${VBOXDRV}.kext is not loaded yet..." >> $LOGFILE 2>&1
        sleep 15
    fi
done

THEDATE=`date "+%Y%m%d%H%M%S"`

# check if VM installed
/Applications/VirtualBox.app/Contents/MacOS/VBoxManage showvminfo $VMNAME >> /dev/null 2>&1
if [ $? -ne 0 ] ; then 
	echo "* [`date`] [$PKG] INFO : Registering $VMNAME" >> $LOGFILE 2>&1
	echo /private/etc/$PKG/bin/xwstartvm.sh --install --name $VMNAME --hda $THISLIVECD $CONTEXTUALIZATION --sshport $SSHPORT >> $LOGFILE 2>&1
	/private/etc/$PKG/bin/xwstartvm.sh --install --name $VMNAME --hda $THISLIVECD $CONTEXTUALIZATION --sshport $SSHPORT >> $LOGFILE 2>&1	
else
	echo "* [`date`] [$PKG] INFO : $VMNAME already registered" >> $LOGFILE 2>&1
fi

echo "* [`date`] [$PKG] INFO : Starting $VMNAME" >> $LOGFILE 2>&1
/private/etc/$PKG/bin/xwstartvm.sh --start --name $VMNAME >> $LOGFILE 2>&1
RC=$?
exit $RC
