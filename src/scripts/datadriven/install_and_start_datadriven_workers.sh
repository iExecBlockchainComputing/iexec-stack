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
# install_and_start_datadriven_workers.sh:
# This scripts prepares and starts 32 workers on localhost
# Each worker has its own directory containing config file and its own copy of monitor_datadriven.sh
# All workers have their own HTTP port
# The script monitor_datadriven.sh is started for each worker
#


MAXWORKERS=32

currentDir=`pwd`
progdir=`dirname $0`
cd $progdir/../..
ROOTDIR=`pwd`
cd $currentDir

CONFDIR=$ROOTDIR/conf
BINDIR=$ROOTDIR/bin

if [ ! -d $CONFDIR ] ; then
	echo "[$0] ERROR: $CONFDIR not found"
	exit 1
fi
if [ ! -d $BINDIR ] ; then
	echo "[$0] ERROR: $BINDIR not found"
	exit 1
fi

WORKERCONFFILENAME=xtremweb.worker.conf
WORKERCONFFILE=$CONFDIR/$WORKERCONFFILENAME


EXPDIRPREFIX=datadrivenexp
rm -Rf $EXPDIRPREFIX*

MONITORFILENAME=monitor_datadriven.sh
MONITORFILE=$progdir/$MONITORFILENAME

PORTFILENAME=httpport.txt

POOLSIZETAG=WORKPOOLSIZE
PORTTAG=HTTPWORKERPORT
PORTVALUE=4324



for (( i = 0 ; i < $MAXWORKERS ; i++ )) ; do
  echo $i
  EXPDIRNAME=$ROOTDIR/$EXPDIRPREFIX-$i
  mkdir $EXPDIRNAME
  THISWORKERCONFFILE=$EXPDIRNAME/$WORKERCONFFILENAME
  grep -vi -E "$POOLSIZETAG|$PORTTAG"  $WORKERCONFFILE > $THISWORKERCONFFILE
  echo "$POOLSIZETAG=1" >> $THISWORKERCONFFILE
  THISPORTVALUE=$(($PORTVALUE+$i))
  echo $THISPORTVALUE > $EXPDIRNAME/$PORTFILENAME
  echo "$PORTTAG=$THISPORTVALUE" >> $THISWORKERCONFFILE
  cp $MONITORFILE $EXPDIRNAME/
  $BINDIR/xtremweb.worker console --xwconfig $THISWORKERCONFFILE > $EXPDIRNAME/worker.log 2>&1 &
  $EXPDIRNAME/$MONITORFILENAME > $EXPDIRNAME/monitor.log 2>&1 &
done

