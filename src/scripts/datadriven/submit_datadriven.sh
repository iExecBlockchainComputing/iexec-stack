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
# submit_datadriven.sh
#    This script submits two job, one for "apptest" application and one for "ls" application. 
#    Each job is replicated 1000 times.
#
# Requirements
#   the data driven package is named "apptestpkg" for "apptest" application: the script ./minitor_datadriven.sh must monitor "apptestpkg"
#   the data driven package is named "lspkg" for "ls" application: the script ./minitor_datadriven.sh must monitor "lspkg"
#



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


CLIENTCONFFILENAME=xtremweb.client.conf
CLIENTCONFFILE=$CONFDIR/$CLIENTCONFFILENAME


LABEL="$(date '+%Y-%m-%d-%H-%M-%S')"

$BINDIR/xwsubmit apptest --xwpackage apptestpkg --xwreplica 1000 --xwreplicasize 40 --xwlabel $LABEL
$BINDIR/xwsubmit ls      --xwpackage lspkg      --xwreplica 1000 --xwreplicasize 40 --xwlabel $LABEL


