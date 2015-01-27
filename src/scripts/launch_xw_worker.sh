#!/bin/sh

WORKERCONF=/tmp/xwworker.conf
ROOT=`dirname $0`/..
LIB=$ROOT/lib
JAR=$LIB/xtremweb.jar
KEY=$ROOT/keystore
WORKERKEY=$KEY/xwhepworker.keys
CONFDIR=$ROOT/conf
WCONF=$CONFDIR/xtremweb.worker.conf


NWORKER=10

date >>  /tmp/xwworkerlaunch.log
echo "Launching $NWORKER workers" >> /tmp/xwworkerlaunch.log

for ((I=1;I<$NWORKER;I++))
do
  echo "Launching worker $i" >> /tmp/xwworkerlaunch.log
  CONFI=/tmp/xwhep-worker-$I
  cp -f $WCONF $CONFI
  if [ $I -eq $NWORKER ]
  then 
    java -Dxtremweb.cache=/tmp -Djava.library.path=/tmp -Djavax.net.ssl.trustStore=$WORKERKEY -cp $JAR xtremweb.worker.Worker --xwconfig $CONFI > /tmp/xwworker.log-$I 2>&1
  else
    java -Dxtremweb.cache=/tmp -Djava.library.path=/tmp -Djavax.net.ssl.trustStore=$WORKERKEY -cp $JAR xtremweb.worker.Worker --xwconfig $CONFI > /tmp/xwworker.log-$I 2>&1 &
  fi
done

