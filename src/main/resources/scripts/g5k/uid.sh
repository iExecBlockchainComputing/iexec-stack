#!/bin/sh

HOSTID=`hostname
    /sbin/ifconfig  |  grep  'inet.*cast'  |  sed  -e 's=^.*inet[^0-9]*\([0-9.]*\).*=\1='
    /sbin/ifconfig  |  grep  '[Ee]ther'    |  sed  -e 's=.*[Ee]ther\(net *HWaddr\)* *\([0-9A-Fa-f:]*\).*=\2='
    uname  -p
    expr   $(cat /proc/cpuinfo  |  grep  processor     |  tail -1  |  sed -e 's=^[^0-9]*==')  +  1
    cat          /proc/cpuinfo  |  grep  'model name'  |  tail -1
    uname  -s
    uname  -r`


#echo hostid=$HOSTID


CORE_MAX=$(cat /proc/cpuinfo  |  grep  processor  |  tail -1  |  sed  -e 's=^[^0-9]*==')
CORE_NUM=0
while  [ "$CORE_NUM" -le "$CORE_MAX" ];  do
  MYUID=`echo "$CORE_NUM $HOSTID"|\
  md5sum  |  \
  sed  -e 's=^\(........\)\(....\)\(....\)\(....\)\(............\).*=\1-\2-\3-\4-\5='`

echo myuid=$MYUID

  CORE_NUM="$(expr  "$CORE_NUM"  +  1)"
done
