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

exec > /tmp/context.log 2>&1

date


#
# install public key to connect as "vmuser", a non privileged user
# it is the user responsibility to copy its public key 
# in the contextualization disk before launching the customizable VM
#
echo "Copying files to /home/vmuser/.ssh"
mkdir -p /home/vmuser/.ssh

# id_rsa.pub is the end user public key to connect to the VM

CONTEXTDIR=/context
# Mount point changed since Sept, 3rd, 2012 
[ -d /mnt/xwcontext ] && CONTEXTDIR=/mnt/xwcontext  

echo "Copying $CONTEXTDIR/id_rsa.pub to /home/vmuser/.ssh/authorized_keys"
cp $CONTEXTDIR/id_rsa.pub /home/vmuser/.ssh/authorized_keys
# Ubuntu LiveCD requires next
cp $CONTEXTDIR/id_rsa.pub /home/vmuser/.ssh/authorized_keys2

chmod 700 /home/vmuser/.ssh/
chmod 600 /home/vmuser/.ssh/*



#
# To create a new customized LiveCD for Scientific Linux 5
# you should call 
# either /usr/local/sbin/sl5_createlivecd.sh
# or    /usr/local/sbin/ubuntu_createlivecd.sh
#
# Please refer to these scripts inline documentation
#