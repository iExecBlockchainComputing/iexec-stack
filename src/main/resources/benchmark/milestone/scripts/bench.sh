#!/bin/sh
#
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
# Trap CTRL + C
#
trap 'echo Terminating ; rm -Rf /mnt/nfs/* ; umount /mnt/nfs ; rm -Rf /mnt/nfsxw/* ; umount /mnt/nfsxw ; exit' 1 2 3 15


thisDir=`dirname $0`


# $1 = count
# $2 = mount point
# $3 comments
transfert ()
{
  echo "/-------------------------------\ "
  echo "| Copying $1  * 8k on NFS $3 XW  |"
  echo "\-------------------------------/"
  date
  dd if=/dev/zero bs=8k count=$1 of=$2/dummy.bin
  sync
  date

  echo "/-------------------------------\ "
  echo "| List file on NFS $2 $3 XW | "
  echo "\-------------------------------/"
  date
  ls -l $2
  sync
  date

  echo "/-------------------------------\ "
  echo "| Erasing $1 on NFS $2 $3 XW | "
  echo "\-------------------------------/"
  date
  rm -f $2/dummy.bin
  sync
  date
  echo ""
}



# $1 = nb files
# $2 = mount point
# $3 comments
creation ()
{
  echo "/----------------------------------------------\ "
  echo "| Creating $1 empty files on NFS $3 XW  |"
  echo "\----------------------------------------------/ "

  iteration=0

  date
  while [ $iteration -lt $1 ]; do
    touch $2/vide$iteration
    iteration=$((iteration + 1))
  done
  sync
  date

  echo "/----------------------------------------------\ "
  echo "| Deleting $1 empty files from NFS $3 XW | "
  echo "\----------------------------------------------/ "
  date
  rm -Rf $2/vide*
  sync
  date
  echo ""
}


# $1 = mountpoint
# $2 = comments
bench ()
{
  mountpoint=$1
  comments=$2

  echo "Mounting NFS $1 $2 XW"
  date
  mount $mountpoint
  date
  echo "NFS mounted $1 $2 XW"
  echo ""
  
  values="10 50 100 250"
  for v in $values; do
    transfert $v $mountpoint $comments
  done

  values="10 50 100 250"
  for v in $values; do
    creation $v $mountpoint $comments
  done

  echo "Umounting NFS $1 $comments XW"
  date
  umount $mountpoint
  date
  echo "NFS umounted $1 $comments XW"
  echo ""
}


# NFS without XtremWeb
#bench /mnt/nfs without

# NFS with XtremWeb
bench /mnt/nfsxw with

