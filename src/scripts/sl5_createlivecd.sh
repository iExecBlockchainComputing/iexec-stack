#!/bin/sh

# Copyrights     : CNRS
# Author         : Simon Dadoun, Oleg Lodygensky
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
#    See https://www.virtualbox.org/wiki/Serial_redirect
#


#  ******************************************************************
#  File    : sl_createlivecd.sh
#  Date    : Jan 6th, 2012
#  Author  : Simon Dadoun; Oleg Lodygensky
# 
#  OS      : Scientific Linux 5 and 5.5
#  Arch    : 32bits
# 
#  Purpose : this script creates a new SL5 LiveCD
#
#  Requirements: xwcontext_prologue and xwcontext_epilogue
#
# -1- The created Live CD contains the following packages
#   - yum rootfiles shadow-utils bind-utils gnupg vim-enhanced vixie-cron 
#   - crontabs passwd which kudzu dhclient pciutils mkisofs 
#   - wget kernel grub dosfstools xorg-x11-apps vim-common cpp gcc jwhois 
#   - zip unzip libX11-devel python-devel libXpm-devel subversion
#   - gcc-gfortran gcc-c++ libgfortran libXft-devel libpng-devel giflib-devel xerces-j2
#   - make automake gcc gcc-c++ kernel-devel
#   - python-devel jzlib zlib perl perl-Compress-Zlib krb5-devel libXmu-devel
#   - libXi-devel mesa-libGL-devel mesa-libGLU-devel mesa-libGLw-devel kbd
#   - system-config-keyboard system-config-display xterm icewm openssh-clients
#   - squashfs-tools aufs jdk-1.6.0_24-fcs.i586 zip unzip rdate.i386 openssh-server sudo
#   - gd-devel bzip2-devel openmotif-devel xauth wget fuse
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


progname=`basename $0`
ROOTDIR=`pwd`

LIVE=/mnt/xwscratch/livecd

USERPKGSNAME="user.packages"
USERPKGSFILE=$ROOTDIR/$USERPKGSNAME

USERHOSTNAME="user.hostname"
USERHOSTNAMEFILE=$ROOTDIR/$USERHOSTNAME

CUSTOMHOSTNAME="xwlivecd_sl.localdomain"
[ -r $USERHOSTNAMEFILE ] && CUSTOMHOSTNAME=`cat $USERHOSTNAMEFILE` 
[ "X$CUSTOMHOSTNAME" = "X" ] && CUSTOMHOSTNAME="xwlivecd_sl.localdomain"


#
# Since August 31st, 2012, we comply to HEPiX Virtualization Working Group
#
INITDIR="/etc/init.d"
PROLOGUE_NAME=xwcontext_prologue
PROLOGUE_FILE=$ROOTDIR/$PROLOGUE_NAME
EPILOGUE_NAME=xwcontext_epilogue
EPILOGUE_FILE=$ROOTDIR/$EPILOGUE_NAME


PROLOGUE_INITD=$INITDIR/$PROLOGUE_NAME
EPILOGUE_INITD=$INITDIR/$EPILOGUE_NAME



#  ******************************************************************
#  buildlivecd()
#  ******************************************************************
buildlivecd()
{
#
# install the build script
#
    echo "Installing the build script"
    mkdir /build
    cd /build
    wget http://www.livecd.ethz.ch/download/build/livecd-3.1.0.tar.gz
    tar zxf livecd-3.1.0.tar.gz

#
# customize the build process
#
    echo "Customizing the build process"
    kernel=`ls /lib/modules`;sed -i "s/^KERNEL.*/KERNEL=$kernel/g" /build/livecd-3.1.0/livecd.conf
    sed -i 's/RUNLEVEL=5/RUNLEVEL=3/g' /build/livecd-3.1.0/livecd.conf
#    sed -i 's/EXTRA_BOOT_OPTIONS=""/EXTRA_BOOT_OPTIONS="nopasswd kb=us changes=xwlivecd750changes"/g' /build/livecd-3.1.0/livecd.conf
#    sed -i 's/EXTRA_BOOT_OPTIONS=""/EXTRA_BOOT_OPTIONS="nopasswd kb=us "/g' /build/livecd-3.1.0/livecd.conf
    sed -i 's/EXTRA_BOOT_OPTIONS=""/EXTRA_BOOT_OPTIONS="nopasswd kb=us console=ttyS0 console=tty0 ignore_loglevel "/g' /build/livecd-3.1.0/livecd.conf

#
# replace sluser by vmuser
#
    echo "replace sluser by vmuser"
    sed -i 's/sluser/vmuser/g' /build/livecd-3.1.0/livecd.conf
    sed -i 's/sluser/vmuser/g' /build/livecd-3.1.0/customize/runlast
    sed -i 's/sluser/vmuser/g' /build/livecd-3.1.0/customize-livecd.sh
    sed -i 's/sluser/vmuser/g' /build/livecd-3.1.0/mini-livecd.sh
    sed -i 's/sluser/vmuser/g' /build/livecd-3.1.0/customize/livecd-autologin

#
# allow sshd and make sure the livecd doesn't ask for configuration at first boot
#
    #this remove sshd from SERVICES_OFF
    sed -i 's/sshd //g' /build/livecd-3.1.0/livecd.conf
    touch /etc/sysconfig/firstboot

#
# customize continued
#
    echo "Customization continued"
    sed -i 's/SCREEN_DEFAULT="1400x1050"/SCREEN_DEFAULT="800x600"/g' livecd-3.1.0/customize/runlast
    for linen in 577 578 579 580 581 582 583 584 585 586 587 588 589 590 591 592 593 596 597 598; do sed -i "$linen s/^/#/" livecd-3.1.0/customize/runlast; done
    sed -i "599 s/^/HOSTNAME=$CUSTOMHOSTNAME/" livecd-3.1.0/customize/runlast
    sed -i "s/localhost\.localdomain localhost/$CUSTOMHOSTNAME localhost/g" livecd-3.1.0/customize/hosts

#
# install this script in /usr/local/sbin/
#
	mkdir -p /usr/local/sbin/
	cp -f /tmp/$progname /usr/local/sbin/
	chown root /usr/local/sbin/$progname
	chmod +x /usr/local/sbin/$progname

#
# allow sudo for this script and poweroff
#
	echo "vmuser ALL = NOPASSWD: /usr/local/sbin/$progname,/sbin/poweroff" >> /etc/sudoers

#
# Prepare livecd-3.1.0/customize/runlast
# for contextualization, random passwords and execute sshd
#
	echo "# XWHEP live cd customization
#

dd if=/dev/urandom count=50|md5sum|passwd --stdin vmuser
dd if=/dev/urandom count=50|md5sum|passwd --stdin root

#
# start sshd
#
/etc/rc.d/init.d/sshd start

#
# Install contextualization
#
chkconfig --add xwcontext_prologue
chkconfig --add xwcontext_epilogue

#
# start contextualization
#
[ -x /etc/init.d/xwcontext_prologue ] && /sbin/service xwcontext_prologue start
[ -x /etc/init.d/xwcontext_epilogue ] && /sbin/service xwcontext_epilogue start


" >> livecd-3.1.0/customize/runlast

#
# deleting /etc/udev/rules.d/*persistent-net.rules 
# because we don't know which network interface to use
# 
	echo "`date` : deleting /etc/udev/rules.d/*persistent-net.rules"
	rm -f /etc/udev/rules.d/*persistent-net.rules

#
# message of the day
#
    echo "Setting message of the day" 
    sed -i 's/Welcome to $LIVECD_OS ${LiveCD}/Welcome to $LIVECD_OS ${LiveCD}, launched by XtremWeb-HEP/g' livecd-3.1.0/customize-livecd.sh

	rm -Rf /tmp/*
#
# build new LiveCD
#
    echo "Building a new LiveCD"
    cd /build/livecd-3.1.0
    ./build-livecd.sh

    echo "Done"
    echo "New LiveCD is here : $LIVE/tmp/livecd.iso"
}



#  ******************************************************************
#  main
#  ******************************************************************

#
# if there is a parameter we are in chroot
#
if [ "$1" == "build" ] ; then 
    buildlivecd
    exit
fi

#yum upgrade
yum -y update

#
# Prepare directory structure
#
echo "Preparing directory structure"
rm -Rf $LIVE
mkdir -p $LIVE
cd $LIVE
mkdir dev proc sys etc tmp
touch etc/fstab etc/mtab

#
# mount virtual file system
#
echo "Mounting virtual file system"
mount --bind /dev dev
mount null -t proc proc
mount null -t sysfs sys

#
# configure yum
#
echo "Configuring yum"
cp -a /etc/yum.conf $LIVE/etc
cp -a /etc/yum.repos.d $LIVE/etc/
mkdir -p $LIVE/var/lock/rpm

#
# install yum
#
echo "yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install yum rootfiles shadow-utils \
bind-utils gnupg vim-enhanced vixie-cron crontabs passwd which kudzu dhclient \
pciutils eject alsa-utils mkisofs wget kernel grub"
yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install yum rootfiles shadow-utils \
bind-utils gnupg vim-enhanced vixie-cron crontabs passwd which kudzu dhclient \
pciutils mkisofs wget kernel grub dosfstools

# required by NEMO and probably by Root and Geant4 applications
echo "`date` : calling yum install xorg-x11-apps"
yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install xorg-x11-apps
echo "`date` : calling yum install vim-common make automake gcc gcc-c++ cpp gcc jwhois zip unzip"
yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install vim-common cpp make automake gcc gcc-c++ gcc jwhois zip unzip
echo "`date` : calling yum install libfreetype6-dev xpmutils libxpm-dev libxpm4 vim subversion build-essential gd-devel bzip2-devel gfortran gcc-c++ libxext-dev libxext6 libxft-dev libpng3 libpng12-dev libgif-dev libgif4 libxerces-c3.1 libxerces-c3-samples libxerces-c3-doc libxerces-c3-dev zlib1g-dev libghc6-bzlib-dev-0.5.0.0-a3a7c libxaw7-dev lesstif2-dev libxmu-dev libxmu-headers libxi-dev libgl1-mesa-dev libglu1-mesa-dev"
yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install libX11-devel libXpm-devel subversion gcc gd-devel bzip2-devel gfortran gcc-c++ libgfortran libXft-devel libpng-devel giflib-devel xerces-j2 jzlib zlib perl perl-Compress-Zlib krb5-devel libXmu-devel libXi-devel mesa-libGL-devel mesa-libGLU-devel mesa-libGLw-devel 
echo "yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install gd-devel bzip2-devel openmotif-devel xauth wget"
yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install gd-devel bzip2-devel openmotif-devel xauth wget

echo "XAuthLocation /usr/bin/xauth" >> /etc/ssh/ssh_config
echo "XAuthLocation /usr/bin/xauth" >> /etc/ssh/sshd_config
echo "X11Forwarding yes" >> /etc/ssh/sshd_config


#
# enable shadow passwords
#
echo "Enabling shadow passwords"
cd $LIVE
sed -i "s|\*|x|" etc/passwd 
sed -i "s|^root::|root:x:|" etc/passwd
cat etc/passwd | cut -d":" -f 1 | while read u; do echo "$u:*:12345:0:99999:1:::"; done >> etc/shadow
chmod 600 etc/shadow
cp -a etc/group etc/gshadow
sed -i "s|:x:|::|" etc/gshadow
sed -i "s|:[0-9]\+:|::|" etc/gshadow
chmod 600 etc/gshadow

#
# install some packages
#
echo "yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install kbd system-config-keyboard system-config-display xterm icewm openssh-clients squashfs-tools aufs jdk-1.6.0_24-fcs.i586 zip unzip rdate.i386 openssh-server fuse-sshfs"
yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install kbd system-config-keyboard system-config-display xterm icewm openssh-clients squashfs-tools aufs jdk-1.6.0_24-fcs.i586 zip unzip rdate.i386 openssh-server sudo fuse-sshfs

#
# install user packages
#
for p in `cat $USERPKGSFILE` ; do 
	echo "yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install $p"
	yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install $p
done


#
# this is the place where you can install custom packages
# they will then be automatically inserterd in the new LiveCD
#
# Example : to create a LiveCD with the XWHEP worker 
# - we first copy the RPM in a contextualization virtual disk
# - the following line tries to find the RPM and eventually installs it
# - of course we must copy to the contextualization disk:
#   + this script 
#   + the worker RPM
# = result : the worker is installed in the new LiveCD
#
#
# install local packages
#
for p in `ls $ROOTDIR/*.rpm` ; do 
	echo "Install $p"
	yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install $p
	if [ $? -eq 0 ] ; then
		echo "Installation successed : $p"
	else
		echo "Installation error : $p"
	fi
done

#
# install system configuration files
#
echo "Installing system configuration files" 
cp /etc/resolv.conf $LIVE/etc
cp /etc/sysconfig/network $LIVE/etc/sysconfig/
cp /etc/sysconfig/i18n $LIVE/etc/sysconfig/
cp /etc/sysconfig/authconfig $LIVE/etc/sysconfig/
cp /etc/sysconfig/kernel $LIVE/etc/sysconfig/
cp /etc/sysconfig/clock $LIVE/etc/sysconfig/

#mkdir -p $LIVE/home/xwhep
#touch $LIVE/home/xwhep/toto

#mkdir -p $LIVE/opt/xwhep
#touch $LIVE/opt/xwhep/toto

if [ ! -f $PROLOGUE_FILE ] ; then
  echo "FATAL : prologue not found ($PROLOGUE_FILE)"
  exit 1
else
  if [ -r $ROOTDIR/$PROLOGUE_NAME ] ; then
    cp $ROOTDIR/$PROLOGUE_NAME $LIVE/$PROLOGUE_INITD
    chmod +x $LIVE/$PROLOGUE_INITD
  fi
fi

if [ ! -f $EPILOGUE_FILE ] ; then
  echo "FATAL : epilogue not found ($EPILOGUE_FILE)"
  exit 1
else
  if [ -r $ROOTDIR/$EPILOGUE_NAME ] ; then
    cp $ROOTDIR/$EPILOGUE_NAME $LIVE/$EPILOGUE_INITD
    chmod +x $LIVE/$EPILOGUE_INITD
  fi
fi

if [ ! -f $ROOTDIR/id_rsa.pub ] ; then
  echo "WARN : pub key not found ($ROOTDIR/id_rsa.pub) : root access not allowed"
else
#
# install public key so that livecd creator can connect as root
# it is the livecd creator responsibility to copy its public key 
# in the contextualization disk before launching the customizable VM
#
  echo "install pub key"
  chmod 600 $ROOTDIR/id_rsa.pub 
  mkdir -p $LIVE/root/.ssh
  chmod 600 $LIVE/root/.ssh
  cp $ROOTDIR/id_rsa.pub  $LIVE/root/.ssh/authorized_keys
  chmod 600 $LIVE/root/.ssh/authorized_keys
  cp $ROOTDIR/id_rsa.pub  $LIVE/root/.ssh/authorized_keys2
  chmod 600 $LIVE/root/.ssh/authorized_keys2
  echo "pub key installed"
fi

if [ ! -f $ROOTDIR/iptables_rules.sh ] ; then
  echo "WARN : iptables rules not found ($ROOTDIR/iptables_rules.sh) : LAN access allowed"
else
#
# install public key so that livecd creator can connect as root
# it is the livecd creator responsibility to copy its public key 
# in the contextualization disk before launching the customizable VM
#
  echo "install iptables_rules"
  cp $ROOTDIR/iptables_rules.sh  $LIVE/root/iptables_rules.sh
  chmod 700 $LIVE/root/iptables_rules.sh
  echo "iptables_rules installed"
fi


#
# enter chroot
#
echo "Entering chroot" 
cp -f $ROOTDIR/* $LIVE/tmp/
cp $0 $LIVE/tmp/
chmod +x $LIVE/tmp/$progname
/usr/sbin/chroot $LIVE /tmp/$progname build
PATH=${PATH}:/sbin
