#!/bin/sh
#
#

if [ "X$JAVA_HOME" = "X" ]; then
	[ -d ~/jdk1.8.0_101/ ] && export JAVA_HOME=~/jdk1.8.0_101/
fi

if [ -z "$JAVA_HOME" ]; then
	echo "JAVA_HOME not set"
	exit 1
fi

echo JAVA_HOME=$JAVA_HOME
java -version

export PATH=$JAVA_HOME/bin/:$PATH

export PATH=$JAVA_HOME/bin/:$PATH
ROOTDIR=`dirname $0`

TOTALCPU=1
CPUNAME=""
OSNAME=`uname`
case $OSNAME in
  Darwin* )
    TOTALCPU=`sysctl -a | grep "logicalcpu:" | cut -d ' ' -f 2`
    CPUNAME=`sysctl  -a | grep cpu.brand_string`
    ;;
  Linux* )
    TOTALCPU=`expr $(cat /proc/cpuinfo  |  grep  processor     |  tail -1  |  sed -e 's=^[^0-9]*==')  +  1`
    CPUNAME=`cat /proc/cpuinfo  |  grep  'model name'  |  tail -1`
    ;;
esac

#
# next helps to determine an UID for the worker
#
HOSTID=`hostname
    /sbin/ifconfig  |  grep  'inet.*cast'  |  sed  -e 's=^.*inet[^0-9]*\([0-9.]*\).*=\1='
    /sbin/ifconfig  |  grep  '[Ee]ther'    |  sed  -e 's=.*[Ee]ther\(net *HWaddr\)* *\([0-9A-Fa-f:]*\).*=\2='
    uname  -p
    echo $TOTALCPU
    echo $CPUNAME
    uname  -s
    uname  -r`

NWORKER=`expr $TOTALCPU - 1`

echo "NWORKER=$NWORKER"

MD5SUM=md5sum
type $MD5SUM > /dev/null 2>&1
[ $? -ne 0 ] && MD5SUM=md5

seq 1 $NWORKER

for I in `seq 1 $NWORKER`
do

  WORKERUID=`echo "$I $HOSTID"|\
  $MD5SUM  |  \
  sed  -e 's=^\(........\)\(....\)\(....\)\(....\)\(............\).*=\1-\2-\3-\4-\5='`

  if [ ! -d /tmp/xwhep-worker-$I ]
  then
    echo "Creating new worker instance"
    echo "cp -r $ROOTDIR /tmp/xwhep-worker-$I"
    cp -r $ROOTDIR /tmp/xwhep-worker-$I
    if [ $? -ne 0 ]; then
	echo "Can't cp -r $ROOTDIR /tmp/xwhep-worker-$I"
	exit 1
    fi
    sed "s/\/tmp\/xwhep-worker/\/tmp\/xwhep-worker-$I/g" /tmp/xwhep-worker-$I/conf/xtremweb.worker.conf > /tmp/tmpconf
    if [ $? -ne 0 ]; then
	echo "Can't sed conf file"
	exit 1
    else
	echo "Conf file sed'ed"
    fi
    mv /tmp/tmpconf /tmp/xwhep-worker-$I/conf/xtremweb.worker.conf
    if [ $? -ne 0 ]; then
	echo "Can't install conf file"
	exit 1
    else
        echo "Conf file installed"
    fi
    echo "UID=$WORKERUID" >> /tmp/xwhep-worker-$I/conf/xtremweb.worker.conf
  else
    echo "Reusing old worker instance"
  fi

  LOGFILE="/tmp/XW-$I-log-`date +'%F-%s'`"
  rm -f $LOGFILE
  touch $LOGFILE
  if [ $I -eq $NWORKER ]
  then 
    echo "Starting final worker"
    /tmp/xwhep-worker-$I/bin/xtremweb.worker console > $LOGFILE 2>&1
  else
    echo "Starting worker $I"
    /tmp/xwhep-worker-$I/bin/xtremweb.worker console > $LOGFILE 2>&1 &
  fi

  if [ $? -ne 0 ]; then
    echo "Can\'t start worker $I"
    exit 1
  fi
done

for i in `ls /tmp/XW-*-log*` ; do
   echo $i
   cat $i
done

