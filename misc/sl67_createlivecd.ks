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
#  File    : sl67_createlivecd.ks
#  Date    : Sept 22nd, 2014
#  Author  : Oleg Lodygensky
# 
#  OS      : Scientific Linux 6.7
#  Arch    : 64bits
# 
#  Purpose : this is the kickstart file to create a new SL 6.7 livecd
#
#  See     : sl67_createlivecd.sh; xwcontext_prologue; xwcontext_epilogue
#
#  Requirements: xwcontext_prologue and xwcontext_epilogue scripts
#
# -1- The created Live CD contains the following packages
#   Please see packages section
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
# Changelog:
# - sept 9th, 2015
#   * "authorized_keys" is preferred to "id_rsa.pub" to allow more than one user to connect as root 
# - dec 4th, 2014
#   * VirtualBox shared file system extension
#
# 
#  !!!!!!!!!!!!!!!!!!!!!!    DO NOT EDIT    !!!!!!!!!!!!!!!!!!!!!!!
#  !!  
#  !! This file is automatically customized by sl64_createlivecd.sh 
#  !!  
#  !!!!!!!!!!!!!!!!!!!!!!    DO NOT EDIT    !!!!!!!!!!!!!!!!!!!!!!!
#  
#  ******************************************************************


#%include /usr/share/livecd-tools/sl67-live-base.ks
install
cdrom
lang en_US.UTF-8
keyboard us
timezone Europe/Paris
auth --useshadow --enablemd5
selinux --permissive
firewall --disabled
text

#
# sends logs to rsyslog
#
# logging --host= --port=514 --level=info



xconfig
services --enabled=network,sshd --disabled=firstboot,ip6tables
network --onboot yes --device eth0 --bootproto dhcp --hostname=%CUSTOMHOSTNAME% --noipv6 

# no root access
rootpw  --lock dummy

authconfig --enableshadow --passalgo=sha512 --enablefingerprint


clearpart --all --drives=sda
part /boot --fstype=ext4 --size=500 --ondisk=sda --asprimary
part pv.5xwrsR-ldgG-FEmM-2Zu5-Jn3O-sx9T-unQUOe --grow --size=500 --ondisk=sda --asprimary

#Very important to have the two part lines before the lvm stuff
volgroup VG --pesize=32768 pv.5xwrsR-ldgG-FEmM-2Zu5-Jn3O-sx9T-unQUOe
logvol / --fstype=ext4 --name=lv_root --vgname=VG --size=40960
logvol /home --fstype=ext4 --name=lv_home --vgname=VG --size=25600
logvol swap --fstype swap --name=lv_swap --vgname=VG --size=4096

#bootloader --location=mbr --driveorder=sda --append="rhgb quiet"
#bootloader --location=mbr --driveorder=sda --append=""
#bootloader --location=mbr --driveorder=sda --append="selinux=0 console=ttyS0 console=tty0 ignore_loglevel"
bootloader --location=mbr --driveorder=sda --append="selinux=0 console=ttyS0 console=tty0 ignore_loglevel" --timeout=1



# SL repositories
repo --name=base          --baseurl=http://ftp.scientificlinux.org/linux/scientific/6.7/$basearch/os/
repo --name=security      --baseurl=http://ftp.scientificlinux.org/linux/scientific/6.7/$basearch/updates/security/
#repo --name=epel-release  --baseurl=http://download.fedoraproject.org/pub/epel/7/$basearch


# or use a mirror close to you
#repo --name=base      --baseurl=http://mirror.switch.ch/ftp/mirror/scientificlinux/6.7/$basearch/os/
#repo --name=security  --baseurl=http://mirror.switch.ch/ftp/mirror/scientificlinux/6.7/$basearch/updates/security/
#repo --name=epel      --baseurl=https://mirrors.fedoraproject.org/metalink?repo=epel-7&arch=$basearch

# fastbugs is disabled
#repo --name=fastbugs  --baseurl=http://ftp.scientificlinux.org/linux/scientific/6.7/$basearch/updates/fastbugs/

firstboot --disabled
group --name=vmuser
user --name=vmuser --groups=vmuser


#
# Packages to build rpm under SL6 ?
# rpmdevtools rpmlint
#
# $> rpmdev-setuptree
# $> rpmdev-newspec
# $> rpmbuild -bb
# $> rpmlint
#
# Following libraries to install cmake 2.8 
# libarchive.so.2 libc.so.6 libcurl.so.4 libdl.so.2 libexpat.so.1 libgcc_s.so.1  libm.so.6 libncurses.so.5 libpthread.so.0 libstdc++.so.6 libtinfo.so.5  libz.so.1
#
#

%packages --nobase --excludedocs
-ModemManager
-alsa-utils
-avahi-libs
-bfa-firmware
-cmake
-crontabs
-cups
-eject
-firefox
-flac
-gnome-keyring
-gnome-themes
-gnome-user-docs
-hicolor-icon-theme
-java-1.5.0-gcj
-mkisofs
-mozilla-filesystem
-rarian
-sendmail
-system-config-users-docs
-thunderbird
-vixie-cron
acpid
anaconda
automake
bind-utils
binutils
bzip2-devel
#cloud-init
#cmake-2.6.4
cpp
dhclient
dosfstools
expat-devel
freetype-devel
fuse
gcc
gcc-c++
gcc-gfortran
gd-devel
giflib-devel
git
glibc-devel
glibc-headers
gnupg
grub
icewm
java-1.7.0-openjdk-devel
jwhois
jzlib
kbd
kernel
kernel-devel
kernel-headers
krb5-devel
libX11-devel
libXft-devel
libXi-devel
libXmu-devel
libXpm-devel
libarchive.so.2
libc.so.6
libcurl.so.4
libdl.so.2
libexpat.so.1
libgcc_s.so.1
libgfortran
libgomp
libm.so.6
libncurses.so.5
libpng-devel
libpthread.so.0
libstdc++.so.6
libtinfo.so.5
libz.so.1
make
mesa-libGL-devel
mesa-libGLU-devel
mesa-libGLw-devel
openmotif-devel
openssh-clients
openssh-server
passwd
patch
pciutils
perl
perl-Compress-Zlib
python-devel
rdate
rootfiles
shadow-utils
squashfs-tools
subversion
sudo
system-config-keyboard
unzip
usermode
vim-common
vim-enhanced
wget
which
xauth
xerces-j2
xorg-x11-apps
xterm
yum
zip
zlib
%end



###############################################################################
# This is run outside chroot. This is executed before pkg section
###############################################################################
%pre
#
# install user's RPM
#
#ls  $ROOTDIR/*.rpm
#if [ $? -eq 0 ] ; then
#  for p in `ls $ROOTDIR/*.rpm` ; do 
#	echo "yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install $p"
#	yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install $p
#	if [ $? -eq 0 ] ; then
#		echo "DONE"
#	else
#		echo "FAILED"
#	fi
#  done
#else
#  echo "No user package"
#fi

%end

###############################################################################
# This is run outside chroot. Here we copy custom files
###############################################################################
%post --nochroot --log=/var/log/sl67_createlivecd.log


TMPDIR=/mnt/xwscratch
find $TMPDIR -type d -iname install_root
if [ ! $? -eq 0 ] ; then
  echo "FATAL : can't find 'install_root' in $TMPDIR"
  exit 1
fi

LIVE=`find $TMPDIR -type d -iname install_root`
echo LIVE=$LIVE
if [ "X" = "X$LIVE" -o  "/" = "$LIVE" ] ; then
  echo "FATAL : can't find 'install_root' in $TMPDIR"
  exit 1
fi


ROOTDIR=`pwd`
INITDIR="/etc/init.d"
PROLOGUE_NAME=xwcontext_prologue
EPILOGUE_NAME=xwcontext_epilogue

LIVEINITD=$LIVE/$INITDIR
echo LIVEINITD=$LIVEINITD

if [ ! -f $ROOTDIR/$PROLOGUE_NAME ] ; then
  echo "WARN : No prologue found ($ROOTDIR/$PROLOGUE_NAME)"
fi

mkdir -p $LIVEINITD
cp $ROOTDIR/$PROLOGUE_NAME $LIVEINITD/
chmod +x $LIVEINITD/$PROLOGUE_NAME


if [ ! -f $ROOTDIR/$EPILOGUE_NAME ] ; then
  echo "WARN : No epilogue found ($ROOTDIR/$EPILOGUE_NAME)"
fi

cp $ROOTDIR/$EPILOGUE_NAME $LIVEINITD/
chmod +x $LIVEINITD/$EPILOGUE_NAME

if [ -f $ROOTDIR/iptables_rules.sh ] ; then
  echo "INFO : iptables rules found : LAN access not allowed"
  mkdir -p $LIVE/root/
  cp $ROOTDIR/iptables_rules.sh $LIVE/root/
  chmod +x $LIVE/root/iptables_rules.sh
else
  echo "WARN : iptables rules not found ($ROOTDIR/iptables_rules.sh) : LAN access allowed"
fi

if [ -f $ROOTDIR/authorized_keys ] ; then
  echo "INFO: authorized_keys found ; root access allowed"
  mkdir -p $LIVE/root/.ssh
  chmod 600 $LIVE/root/.ssh
  cp $ROOTDIR/authorized_keys  $LIVE/root/.ssh/authorized_keys
  cp $ROOTDIR/authorized_keys  $LIVE/root/.ssh/authorized_keys2
  chmod 600 $LIVE/root/.ssh/authorized_keys*
else
  echo "WARN : $ROOTDIR/authorized_keys not found ; root access not allowed"
fi


echo "Configuring yum"
cp -a /etc/yum.conf $LIVE/etc
cp -a /etc/yum.repos.d $LIVE/etc/
mkdir -p $LIVE/var/lock/rpm


#
# install user's packages
#
USERPKGSNAME="user.packages"
USERPKGSFILE=$ROOTDIR/$USERPKGSNAME
if [ -f $USERPKGSFILE ] ; then 
  for p in `cat $USERPKGSFILE` ; do 
	echo "yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install $p"
	yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install $p
	if [ $? -eq 0 ] ; then
		echo "DONE"
	else
		echo "FAILED"
	fi
  done
else
  echo "No user packages list"
fi

#
# install user's RPM
#
ls  $ROOTDIR/*.rpm
if [ $? -eq 0 ] ; then
  for p in `ls $ROOTDIR/*.rpm` ; do 
	echo "yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install $p"
	yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install $p
	if [ $? -eq 0 ] ; then
		echo "DONE"
	else
		echo "FAILED"
	fi
  done
else
  echo "No user package"
fi

#
# install user's archive in /usr/local/
#
mkdir -p $LIVE/usr/local/

ls  $ROOTDIR/*.tar.gz
if [ $? -eq 0 ] ; then
  for p in `ls $ROOTDIR/*.tar.gz` ; do 
	echo -n "tar xvfz $p : "
	cd $LIVE/usr/local/ && tar xvfz $p
	if [ $? -eq 0 ] ; then
		echo "DONE"
	else
		echo "FAILED"
	fi
  done
else
  echo "No user tar.gz archive"
fi

ls  $ROOTDIR/*.tar.bz2
if [ $? -eq 0 ] ; then
  for p in `ls $ROOTDIR/*.tar.bz2` ; do 
	echo -n "tar xvfj $p : "
	cd $LIVE/usr/local/ && tar xvfj $p
	if [ $? -eq 0 ] ; then
		echo "DONE"
	else
		echo "FAILED"
	fi
  done
else
  echo "No user tar.bz2 archive"
fi

ls  $ROOTDIR/*.zip
if [ $? -eq 0 ] ; then
  for p in `ls $ROOTDIR/*.zip` ; do 
	echo -n "unzip $p : "
	cd $LIVE/usr/local/ && unzip $p
	if [ $? -eq 0 ] ; then
		echo "DONE"
	else
		echo "FAILED"
	fi
  done
else
  echo "No user zip archive"
fi


# Under SL7, cloud-init can't be installed from pkg list, but by hand only ( ?!?! )
# install cloud-init
#
echo "yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install cloud-init"
yum -y -c $LIVE/etc/yum.conf --installroot=$LIVE install cloud-init
if [ $? -eq 0 ] ; then
       echo "INFO: cloud-init installed"
else
       echo "WARN: cloud-init installation error"
fi


#
# install VirtualBox extensions
#

cp -rf  /media/VBOXADDITIONS*/ $LIVE/usr/local/
if [ $? -eq 0 ] ; then
	echo "VBox Additions copied to $LIVE/usr/local/"
else
	echo "VBox Additions not found"
fi

exit 0

%end


###############################################################################
# This is run inside chroot. Here, we install custom files
###############################################################################
%post --log=/var/log/sl67_createlivecd.log

#
# install VirtualBox extensions
#

yum -y install kernel-devel kernel-headers

if [ -r /usr/local/VBOXADDITIONS*/VBoxLinuxAdditions.run ] ; then 
	VBLA=`ls /usr/local/VBOXADDITIONS*/VBoxLinuxAdditions.run | tail -1`
	if [ ! -z "$VBLA" ] ; then
		sh $VBLA
		if [ $? -eq 0 ] ; then
			logger -t xwhep-create-livecd -s "VBox Additions correctly installed"
		else
			logger -t xwhep-create-livecd -s "VBox Additions installation error"
		fi
	else
		logger -t xwhep-create-livecd -s "VBox Additions not found"
	fi
fi

/sbin/chkconfig acpid on
/sbin/chkconfig cloud-init on
/sbin/chkconfig cloud-init-local on
echo "NOZEROCONF=yes" >> /etc/sysconfig/network
rm    /etc/udev/rules.d/70-persistent-net.rules
touch /etc/udev/rules.d/70-persistent-net.rules
   
#
# Clean VirtualBox installation requirements
# Sept 10th, 2015 : we don't because it removes gcc
#rm -Rf /usr/local/VBOXADDITIONS*
#yum -y erase kernel-devel kernel-headers
yum -y clean


#
# Install user pub key for root access
#
if [ -f /root/.ssh/authorized_keys ] ; then
  logger -t xwhep-create-livecd -s "INFO : pub key found : root access allowed"
else
  logger -t xwhep-create-livecd -s "WARN : pub key not found : root access not allowed"
fi

#
# configure firewall
#
if [ -f /root/iptables_rules.sh ] ; then
	logger -t xwhep-create-livecd -s "INFO : iptables rules found, LAN access not allowed"
	chmod +x /root/iptables_rules.sh
	/root/iptables_rules.sh > /root/iptables_rules.out
else
	logger -t xwhep-create-livecd -s "WARN : iptables rules not found : LAN access allowed"
fi

#
# configure sudoers
#
echo "vmuser ALL = NOPASSWD: /sbin/poweroff" >> /etc/sudoers

#
# insert "vmuser" into "fuse" group
#
usermod -G fuse vmuser

#
# add VMUSER user
#
#/usr/sbin/groupadd -f vmuser
#/usr/sbin/adduser vmuser -g vmuser -G fuse -m -s /bin/bash -d /home/vmuser

chmod +x /etc/init.d/xwcontext_prologue
chmod +x /etc/init.d/xwcontext_epilogue


/sbin/chkconfig --add xwcontext_prologue
/sbin/chkconfig --add xwcontext_epilogue

[ -x /etc/init.d/xtremweb.server ] && /sbin/chkconfig --add xtremweb.server
[ -x /etc/init.d/xtremweb.worker ] && /sbin/chkconfig --add xtremweb.worker

[ -x /etc/init.d/firstboot ] && /sbin/chkconfig firstboot off
echo "RUN_FIRSTBOOT=NO" > /etc/sysconfig/firstboot

[ -x /etc/init.d/sshd ] && /sbin/chkconfig --add sshd
[ -x /etc/init.d/sshd ] && /sbin/chkconfig sshd on
ifconfig
exit 0
%end
