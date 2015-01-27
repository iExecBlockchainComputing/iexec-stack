#!/bin/sh
export JAVA_HOME=~/java-6-openjdk/
export PATH=$JAVA_HOME/bin/:$PATH
ROOTDIR=`dirname $0`

#
# next helps to determine an UID for the worker
#
HOSTID=`hostname
    /sbin/ifconfig  |  grep  'inet.*cast'  |  sed  -e 's=^.*inet[^0-9]*\([0-9.]*\).*=\1='
    /sbin/ifconfig  |  grep  '[Ee]ther'    |  sed  -e 's=.*[Ee]ther\(net *HWaddr\)* *\([0-9A-Fa-f:]*\).*=\2='
    uname  -p
    expr   $(cat /proc/cpuinfo  |  grep  processor     |  tail -1  |  sed -e 's=^[^0-9]*==')  +  1
    cat          /proc/cpuinfo  |  grep  'model name'  |  tail -1
    uname  -s
    uname  -r`



if [ -z $1 ]
then
    NWORKER=`cat /proc/cpuinfo | grep proc | awk '{max=$3}END{print max+1}'`
else
    NWORKER=$1
fi

for I in `seq 1 $NWORKER`
do

  WORKERUID=`echo "$CORE_NUM $HOSTID"|\
  md5sum  |  \
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
