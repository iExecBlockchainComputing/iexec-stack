#!/bin/sh
#
#
if [ -z "$JAVA_HOME" ]; then
	echo "JAVA_HOME not set"
	exit 1
fi

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

MD5SUM=md5sum
type $MD5SUM > /dev/null 2>&1
[ $? -ne 0 ] && MD5SUM=md5

for I in `seq 1 $NWORKER`
do

  WORKERUID=`echo "$I $HOSTID"|\
  $MD5SUM  |  \
  sed  -e 's=^\(........\)\(....\)\(....\)\(....\)\(............\).*=\1-\2-\3-\4-\5='`

  if [ ! -d /tmp/xwhep-worker-$I ]
  then
    echo "Creating new worker instance"
    cp -r $ROOTDIR /tmp/xwhep-worker-$I
    sed "s/\/tmp\/xwhep-worker/\/tmp\/xwhep-worker-$I/g" /tmp/xwhep-worker-$I/conf/xtremweb.worker.conf > /tmp/tmpconf
    mv /tmp/tmpconf /tmp/xwhep-worker-$I/conf/xtremweb.worker.conf
    echo "UID=$WORKERUID" >> /tmp/xwhep-worker-$I/conf/xtremweb.worker.conf
  else
    echo "Reusing old worker instance"
  fi
  if [ $I -eq $NWORKER ]
  then 
    /tmp/xwhep-worker-$I/bin/xtremweb.worker console > /tmp/XW-$I-log-`date +'%F-%s'` 2>&1
  else
    /tmp/xwhep-worker-$I/bin/xtremweb.worker console > /tmp/XW-$I-log-`date +'%F-%s'` 2>&1 &
  fi
done
