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
# install_and_start_datadriven_server.sh: this starts the server on localhost
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


SERVERCONFFILENAME=xtremweb.server.conf
SERVERCONFFILE=$CONFDIR/$SERVERCONFFILENAME



cp -f $SERVERCONFFILE $SERVERCONFFILE-sav
grep -vi "LAUNCHERURL"  $SERVERCONFFILE > THISSERVERCONFFILE
mv -f THISSERVERCONFFILE $SERVERCONFFILE

$BINDIR/xtremweb.server console -DHOMEDIR=$ROOTDIR/tmp/ > server.log 2>&1 &


