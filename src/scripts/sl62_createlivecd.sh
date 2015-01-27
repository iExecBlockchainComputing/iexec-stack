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

#
# Change log:
# Jan 24th, 2014:
# author : Oleg Lodygensky
#    get kernel console for debugging boot process, if needed 
#    See sl62_createlivecd.ks
#


#  ******************************************************************
#  File    : sl62_createlivecd.sh
#  Date    : August 30th, 2012
#  Author  : Oleg Lodygensky
# 
#  OS      : Scientific Linux 6.2
#  Arch    : 32bits
# 
#  Purpose : this script creates a new SL6 LiveCD
#
#  See     : sl62_createlivecd.ks; xwcontext_prologue; xwcontext_epilogue
#
#  Requirements: xwcontext_prologue and xwcontext_epilogue scripts
#
# -1- The created Live CD contains the following packages
#   - yum rootfiles shadow-utils bind-utils gnupg vim-enhanced vixie-cron 
#   - crontabs passwd which kudzu dhclient pciutils eject alsa-utils mkisofs 
#   - wget kernel grub dosfstools xorg-x11-apps vim-common cpp gcc jwhois 
#   - zip unzip libX11-devel python-devel freetype-devel libXpm-devel subversion
#   - gcc-gfortran gcc-c++ libgfortran libXft-devel libpng-devel giflib-devel xerces-j2
#   - make automake gcc gcc-c++ kernel-devel
#   - python-devel jzlib zlib perl perl-Compress-Zlib krb5-devel libXmu-devel
#   - libXi-devel mesa-libGL-devel mesa-libGLU-devel mesa-libGLw-devel kbd
#   - system-config-keyboard system-config-display xterm icewm openssh-clients
#   - squashfs-tools aufs jdk-1.6.0_24-fcs.i586 zip unzip rdate.i386 openssh-server sudo
#   - gd-devel bzip2-devel openmotif-devel xauth
#
# -2- The created Live CD is configured as follow:
#   - Root access denied except if id_rsa.pub is provided at LiveCD creation time
#     (see -3- below)
#   - network access customized if iptables_rules.sh provided
#     (see -3- below)
#   - a non privileged user "vmuser"
#   - mount points : see xwcontext_prologue.sh
#
#  -3- Optional files may be installed in the resulted LiveCD
#     - id_rsa.pub installed in /root/.ssh/authorized_keys2 so that the live cd creator may connect
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
KSFILE=$ROOTDIR/sl62_createlivecd.ks
KSCFILENAME=$ROOTDIR/sl62_createlivecd.ks_customhostname
KSCFILE=/tmp/$KSCFILENAME

PROLOGUE_NAME=xwcontext_prologue
PROLOGUE_FILE=$ROOTDIR/$PROLOGUE_NAME
EPILOGUE_NAME=xwcontext_epilogue
EPILOGUE_FILE=$ROOTDIR/$EPILOGUE_NAME

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

if [ ! -f $ROOTDIR/id_rsa.pub ] ; then
  echo "WARN : pub key not found ($ROOTDIR/id_rsa.pub) : root access not allowed"
fi

if [ ! -f $ROOTDIR/iptables_rules.sh ] ; then
  echo "WARN : iptables rules not found ($ROOTDIR/iptables_rules.sh) : LAN access allowed"
fi

#
# Custom host name
#
USERHOSTNAME="user.hostname"
USERHOSTNAMEFILE=$CURRENTDIR/$USERHOSTNAME
CUSTOMHOSTNAME="xwlivecd_sl.localdomain"
if [ -r $USERHOSTNAMEFILE ] ; then
  CUSTOMHOSTNAME=`cat $USERHOSTNAMEFILE` 
  echo "INFO : custom host name found : $CUSTOMHOSTNAME"
else
  echo "INFO : custom host name not found; using default $CUSTOMHOSTNAME"
fi
cat $KSFILE | sed "s/%CUSTOMHOSTNAME%/$CUSTOMHOSTNAME/g" > $KSCFILE

#setarch amd64 LANG=C livecd-creator --config=$KSCFILE --fslabel=XWCD-sl62_$CUSTOMHOSTNAME --tmpdir=$LIVETMPDIR

yum -y update
#yum -y --enablerepo=sl-addons install liveusb-creator
#yum -y install livecd-tools

yum -y install revisor-cli

LIVETMPDIR=/mnt/xwscratch/livecdsl6/$CUSTOMHOSTNAME
mkdir -p $LIVETMPDIR
if [ $? -eq 0 ] ; then
  echo "LANG=C livecd-creator --config=$KSCFILE --fslabel=XWCD-sl62_$CUSTOMHOSTNAME --tmpdir=$LIVETMPDIR"
  sleep 1
  LANG=C livecd-creator --config=$KSCFILE --fslabel=XWCD-sl62_$CUSTOMHOSTNAME --tmpdir=$LIVETMPDIR
else
  echo "FATAL : can't create dir $LIVETMPDIR"
fi
