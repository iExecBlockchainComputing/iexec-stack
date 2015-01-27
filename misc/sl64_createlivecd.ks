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
#  File    : sl64_createlivecd.ks
#  Date    : Sept 7th, 2012
#  Author  : Oleg Lodygensky
# 
#  OS      : Scientific Linux 6
#  Arch    : 32bits
# 
#  Purpose : this is the kickstart file to create a new SL 6 livecd
#
#  See     : sl64_createlivecd.sh; xwcontext_prologue; xwcontext_epilogue
#
#  Requirements: xwcontext_prologue and xwcontext_epilogue scripts
#
# -1- The created Live CD contains the following packages
#   Please see packages section
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
#  !!!!!!!!!!!!!!!!!!!!!!    DO NOT EDIT    !!!!!!!!!!!!!!!!!!!!!!!
#  !!  
#  !! This file is automatically customized by sl64_createlivecd.sh 
#  !!  
#  !!!!!!!!!!!!!!!!!!!!!!    DO NOT EDIT    !!!!!!!!!!!!!!!!!!!!!!!
#  
#  ******************************************************************


#%include /usr/share/livecd-tools/sl62-live-base.ks
install
cdrom
lang en_US.UTF-8
keyboard us
timezone Europe/Paris
auth --useshadow --enablemd5
selinux --enforcing
firewall --enabled --service=mdns,ssh,http
text

xconfig
services --enabled=NetworkManager,network,sshd --disabled=firstboot,ip6tables
network --onboot yes --device eth0 --bootproto dhcp --noipv6  --hostname=%CUSTOMHOSTNAME%

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
bootloader --location=mbr --driveorder=sda --append="selinux=0 console=ttyS0 console=tty0 ignore_loglevel"



# SL repositories
repo --name=base      --baseurl=http://ftp.scientificlinux.org/linux/scientific/6.4/$basearch/os/
repo --name=security  --baseurl=http://ftp.scientificlinux.org/linux/scientific/6.4/$basearch/updates/security/

# or use a mirror close to you
#repo --name=base      --baseurl=http://mirror.switch.ch/ftp/mirror/scientificlinux/6.4/$basearch/os/
#repo --name=security  --baseurl=http://mirror.switch.ch/ftp/mirror/scientificlinux/6.4/$basearch/updates/security/

# fastbugs is disabled
#repo --name=fastbugs  --baseurl=http://ftp.scientificlinux.org/linux/scientific/6.4/$basearch/updates/fastbugs/

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

%packages --nobase --excludedocs
-sendmail
-cups
-cmake
make
patch
expat-devel
anaconda
rootfiles
shadow-utils
bind-utils
gnupg
vim-enhanced
-vixie-cron
-crontabs
passwd
which
dhclient
pciutils
-eject
-alsa-utils
-mkisofs
wget
kernel
grub
dosfstools
xorg-x11-apps
vim-common
cpp
gcc
jwhois
zip
unzip
libX11-devel
freetype-devel
libXpm-devel
subversion
gcc-gfortran
gcc-c++
libgfortran
libXft-devel
libpng-devel
giflib-devel
xerces-j2
make
automake
gcc
gcc-c++
-kernel-devel
python-devel
jzlib
zlib
perl
perl-Compress-Zlib
krb5-devel
libXmu-devel
libXi-devel
mesa-libGL-devel
mesa-libGLU-devel
mesa-libGLw-devel
kbd
system-config-keyboard
xterm
icewm
openssh-clients
squashfs-tools
-java-1.5.0-gcj
java-1.7.0-openjdk
#java-1.7.0-openjdk-devel
zip
unzip
rdate
openssh-server
sudo
gd-devel
bzip2-devel
openmotif-devel
xauth
wget
fuse
yum
-gnome-themes
-gnome-user-docs
-system-config-users-docs
-thunderbird
-firefox
%end

###############################################################################
# This is run outside chroot; this copies custom files
###############################################################################
%post --nochroot

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
  echo "FATAL : No prologue found ($ROOTDIR/$PROLOGUE_NAME)"
  exit 1
fi

mkdir -p $LIVEINITD
cp $ROOTDIR/$PROLOGUE_NAME $LIVEINITD/$PROLOGUE_INITD
chmod +x $LIVEINITD/$PROLOGUE_INITD


if [ ! -f $ROOTDIR/$EPILOGUE_NAME ] ; then
  echo "FATAL : No epilogue found ($ROOTDIR/$EPILOGUE_NAME)"
  exit 1
fi

cp $ROOTDIR/$EPILOGUE_NAME $LIVEINITD/$EPILOGUE_INITD
chmod +x $LIVEINITD/$EPILOGUE_INITD

if [ -f $ROOTDIR/iptables_rules.sh ] ; then
  echo "INFO : iptables rules found : LAN access not allowed"
  mkdir -p $LIVE/root/
  cp $ROOTDIR/iptables_rules.sh $LIVE/root/
  chmod +x $LIVE/root/iptables_rules.sh
else
  echo "WARN : iptables rules not found ($ROOTDIR/iptables_rules.sh) : LAN access allowed"
fi

if [ -f $ROOTDIR/id_rsa.pub ] ; then
  echo "INFO: pub key found : root access allowed"
  mkdir -p $LIVE/root/.ssh
  chmod 600 $LIVE/root/.ssh
  cp $ROOTDIR/id_rsa.pub  $LIVE/root/.ssh/authorized_keys
  cp $ROOTDIR/id_rsa.pub  $LIVE/root/.ssh/authorized_keys2
  chmod 600 $LIVE/root/.ssh/authorized_keys*
else
  echo "WARN : pub key not found ($ROOTDIR/id_rsa.pub) : root access not allowed"
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

exit 0

%end


###############################################################################
# This is run inside chroot; this installs custom files
###############################################################################
%post

#
# configure firewall
#
if [ -f /root/iptables_rules.sh ] ; then
  echo "INFO : LAN access not allowed"
  chmod +x /root/iptables_rules.sh
  /root/iptables_rules.sh > /root/iptables_rules.out
else
  echo "WARN : no iptables rules : LAN access allowed"
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


exit 0
%end
