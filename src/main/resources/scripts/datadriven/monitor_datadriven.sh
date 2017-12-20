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
# Requirements:
#   ./httpport.txt must exist and contain the worker http port
#   the data driven package must be named "apptestpkg" and "lspkg" (see ./submit_datadriven.sh)
#
# monitor_data.sh:
# This script monitors data to be computed by the XWHEP worker
# 
# Pseudo code
# for ever do
#    - creates a new directory
#    - access http://localhost:$PORT/shareddata/?datapackagename=blabla&datapackagepath=blabla
#    - while result file does not exist do
#         wait 1s
#      done
# done
#


MAXWORKERS=8
ROOTDIR=`dirname $0`

PORTFILENAME=httpport.txt
PORTFILE=$ROOTDIR/$PORTFILENAME

if [ ! -r $PORTFILE ] ; then
  echo "$PORTFILE : not found"
  exit 1
fi

PORT=`cat $PORTFILE`

HTTPREQUESTHEADER="http://localhost:$PORT/shareddata/?datapackagename=lspkg,apptestpkg&datapackagepath="

#
# waiting for the worker to be ready
#

GOTIT=1
while [ $GOTIT -ne 0 ] ; do
  GOTIT=0
  HTTPREQUEST="http://localhost:$PORT"
  echo $HTTPREQUEST
  curl "$HTTPREQUEST" > /dev/null 2>&1
  if [ $? -ne 0 ] ; then
    GOTIT=1
    echo "WARN: $HTTPREQUEST  not responding"
    echo "WARN: waiting 5s"
    sleep 5
  fi

done

LSRESULTFILENAME=stdout.txt
APPTESTRESULTFILENAME=TestResults.txt

DIRLABEL="$(date '+%Y-%m-%d-%H-%M-%S')"

#
# Infinite loop
#

LSRESULTFILE=lsresult_tostart_$RANDOM
APPTESTRESULTFILE=apptestresult_tostart_$RANDOM

FIRSTTIME=1

LSCOUNTER=0
APPTESTCOUNTER=0


while true ; do

  if [ -f $LSRESULTFILE -o $FIRSTTIME -eq 1 ] ; then
    echo "LS result file ($LSRESULTFILE) found; generating new one"
    LSOUTPUTDIR=$TMPDIR/ls_$DIRLABEL_$LSCOUNTER
    LSCOUNTER=$(($LSCOUNTER+1))
#    rm -Rf $LSOUTPUTDIR
    mkdir  $LSOUTPUTDIR
    # just to see...
    cp $PORTFILE $LSOUTPUTDIR
  fi

  if [ -f $APPTESTRESULTFILE -o $FIRSTTIME -eq 1 ] ; then
    echo "APPTEST result file ($APPTESTRESULTFILE) found; generating new one"
    APPTESTOUTPUTDIR=$TMPDIR/apptest_$DIRLABEL_$APPTESTCOUNTER
    APPTESTCOUNTER=$(($APPTESTCOUNTER+1))
#    rm -Rf $APPTESTOUTPUTDIR
    mkdir  $APPTESTOUTPUTDIR
  fi

  FIRSTTIME=0

  date
 
  echo "LS      OutputDir=$LSOUTPUTDIR"
  echo "APPTEST OutputDir=$APPTESTOUTPUTDIR"

#
# Sending the HTTP request
#
  HTTPREQUEST=$HTTPREQUESTHEADER$LSOUTPUTDIR,$APPTESTOUTPUTDIR
  echo "curl $HTTPREQUEST"
  curl "$HTTPREQUEST" > /dev/null 2>&1
  if [ $? -ne 0 ] ; then
    echo "ERROR: http://localhost:$PORT not responding"
    exit 1
  fi


#
# Waiting for the result
#
  LSRESULTFILE=$LSOUTPUTDIR/$LSRESULTFILENAME
  APPTESTRESULTFILE=$APPTESTOUTPUTDIR/$APPTESTRESULTFILENAME
  printf "Waiting $LSRESULTFILE $APPTESTRESULTFILE"


  GOTRESULT=0
  while [ $GOTRESULT -eq 0 ] ; do
    CHECKHTTPREQUEST="http://localhost:$PORT"
    curl "$CHECKHTTPREQUEST" > /dev/null 2>&1
    if [ $? -ne 0 ] ; then
      echo "ERROR: http://localhost:$PORT not responding"
      exit 1
    fi

    if [ -f $LSRESULTFILE ] ; then
      echo "LS result file ($LSRESULTFILE) found"
      GOTRESULT=1
    fi
   
    if [ -f $APPTESTRESULTFILE ] ; then
      echo "APPTEST result file ($APPTESTRESULTFILE) found"
      GOTRESULT=1
    fi


    printf "."
    sleep 1
  done

  echo ""

done

