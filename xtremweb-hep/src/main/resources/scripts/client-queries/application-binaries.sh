#!/bin/bash
IFS='"'

CFG=""
DOWNLOAD=""
XML="--xwformat xml"

while [ $# -gt 0 ]; do
    case $1 in
        "--xwconfig" )
        shift
        CFG="--xwconfig $1"
    ;;
    "--xwdownload" )
        DOWNLOAD="--xwdownload"
;;
    esac
    shift
done


xwapps $CFG $XML | \
perl -pwe "s/\" /\"\n  /g"| \
grep -E "uri|name" | \
while read attr ; do
    echo $attr | grep 'name=' > /dev/null 2>&1
    if [ $? -eq 0 ] ; then
        echo ""
        echo "Application $attr"
        continue
    fi
    [ "X$attr" = "X" ] && continue
#    echo pouet $attr
    uri=`echo $attr | cut -d "=" -f 2 | sed "s/ //g" `
    [ "X$uri" = "X" ] && continue
#    echo pouet $attr $uri
#    echo "xwdatas $CFG $XML \"$uri\""
    echo ""
    xwdatas $CFG $DOWNLOAD $XML $uri | \
    perl -pwe "s/\" /\"\n  /g"| \
    grep -E "name|uri|size|md5|Downloaded" | sed "s/^Downloaded/  Downloaded/"
    [ $? -ne 0 ] && echo " $uri ERROR"
#    echo ""
done
