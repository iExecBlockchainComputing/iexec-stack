#!/bin/sh

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
#

#  ******************************************************************
#  File    : sl65_createlivecd.sh
#  Date    : Septembre 22nd, 2014
#  Author  : Oleg Lodygensky
# 
#  OS      : Scientific Linux 6.5
#  Arch    : 32bits
# 
#  Purpose : this script creates a new SL6 LiveCD
#
#  See     : sl65_createlivecd.ks; xwcontext_prologue; xwcontext_epilogue
#
#  Requirements: xwcontext_prologue and xwcontext_epilogue scripts
#
# -1- The created Live CD contains the following packages
#   - Please refer to sl65_createlivecd.ks 
#
# -2- The created Live CD is configured as follow:
#   - Root access denied except if authorized_keys is provided at LiveCD creation time
#     (see -3- below)
#   - network access customized if iptables_rules.sh provided
#     (see -3- below)
#   - a non privileged user "vmuser"
#   - mount points : see xwcontext_prologue.sh
#
#  -3- Optional files may be installed in the resulted LiveCD
#     - authorized_keys installed in /root/.ssh/authorized_keys2 to allow root connection
#     - iptables_rules.sh installed in /root/
#     - user.packages, a text file, containing a list of optional packages to install
#     - user.hostname, a text file, containing the expected host name
#     - *.rpm are installed
# 
#  !!!!!!!!!!!!!!!!    DO NOT EDIT    !!!!!!!!!!!!!!!!
#  
#  ******************************************************************

CURRENTDIR=`pwd`
ROOTDIR=`dirname $0`
KSFILE=$ROOTDIR/sl65_createlivecd.ks
KSCFILENAME=$ROOTDIR/sl65_createlivecd.ks_customhostname
KSCFILE=/tmp/$KSCFILENAME

PROLOGUE_NAME=xwcontext_prologue
PROLOGUE_FILE=$ROOTDIR/$PROLOGUE_NAME
EPILOGUE_NAME=xwcontext_epilogue
EPILOGUE_FILE=$ROOTDIR/$EPILOGUE_NAME

CMAKE_RPMFILENAME=cmake-2.6.4-5.el6.x86_64.rpm
CMAKE_RPMFILE=$ROOTDIR/$CMAKE_RPMFILENAME


if [ ! -f $KSFILE ] ; then
  echo "FATAL : kickstart file not found ($KSFILE)"
  exit 1
fi
if [ ! -f $PROLOGUE_FILE ] ; then
  echo "FATAL : prologue not found ($PROLOGUE_FILE)"
  exit 1
fi
if [ ! -f $EPILOGUE_FILE ] ; then
  echo "FATAL : epilogue not found ($EPILOGUE_FILE)"
  exit 1
fi

if [ ! -f $ROOTDIR/authorized_keys ] ; then
  echo "WARN : pub key not found ($ROOTDIR/authorized_keys) : root access not allowed"
fi

if [ ! -f $ROOTDIR/iptables_rules.sh ] ; then
  echo "WARN : iptables rules not found ($ROOTDIR/iptables_rules.sh) : LAN access allowed"
fi

if [ ! -f CMAKE_RPMFILE ] ; then
  echo "WARN : $CMAKE_RPMFILENAME not found ($CMAKE_RPMFILE) : cmake will not be installed"
fi

#
# Custom host name
#
USERHOSTNAME="user.hostname"
USERHOSTNAMEFILE=$CURRENTDIR/$USERHOSTNAME
CUSTOMHOSTNAME="xwlivecd_sl65.localdomain"
if [ -r $USERHOSTNAMEFILE ] ; then
  CUSTOMHOSTNAME=`cat $USERHOSTNAMEFILE` 
  echo "INFO : custom host name found : $CUSTOMHOSTNAME"
else
  echo "INFO : custom host name not found; using default $CUSTOMHOSTNAME"
fi
cat $KSFILE | sed "s/%CUSTOMHOSTNAME%/$CUSTOMHOSTNAME/g" > $KSCFILE

#setarch amd64 LANG=C livecd-creator --config=$KSCFILE --fslabel=XWCD-sl65_$CUSTOMHOSTNAME --tmpdir=$LIVETMPDIR

yum -y update
#yum -y --enablerepo=sl-addons install liveusb-creator
#yum -y install livecd-tools

yum -y install revisor-cli

LIVETMPDIR=/mnt/xwscratch/livecdsl6/$CUSTOMHOSTNAME
mkdir -p $LIVETMPDIR
if [ $? -eq 0 ] ; then
  echo "LANG=C livecd-creator --config=$KSCFILE --fslabel=$CUSTOMHOSTNAME --tmpdir=$LIVETMPDIR"
  sleep 1
  LANG=C livecd-creator --config=$KSCFILE --fslabel=$CUSTOMHOSTNAME --tmpdir=$LIVETMPDIR
else
  echo "FATAL : can't create dir $LIVETMPDIR"
fi
