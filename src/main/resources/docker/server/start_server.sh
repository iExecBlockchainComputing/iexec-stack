#!/bin/sh

#
# -1- don't renice in container
# -2- we must remove LAUNCHERURL since Apache is not installed
#
sed -i "s/^V_NICE=.*//g" /xwhep/bin/xtremwebconf.sh
sed -i "s/LAUNCHER.*//g" /xwhep/conf/xtremweb.server.conf

# change DBHOST value in the config if defined
if [ ! -z $DBHOST ] ; then
  sed -i "s/^DBHOST=.*/DBHOST=$DBHOST/g" /xwhep/conf/xtremweb.server.conf
	sed -i "s/^DBHOST=.*/DBHOST=$DBHOST/g" /xwhep/conf/xwconfigure.values
fi

# keystore is generated from the script xwhepgenkeys directly in the container
rm /xwhep/keystore/cacerts
rm /xwhep/keystore/*.keys
rm /xwhep/keystore/*.p12
/xwhep/bin/xwhepgenkeys
if [ $? -eq 0 ] ; then echo "Keystores generated" ; else echo "ERROR: keystores generation error"; exit 1 ;
fi

/xwhep/bin/xtremweb.server console
