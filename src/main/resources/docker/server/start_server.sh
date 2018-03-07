#!/bin/sh

#
# -1- don't renice in container
# -2- we must remove LAUNCHERURL since Apache is not installed
#
sed -i "s/^V_NICE=.*//g" /iexec/bin/xtremwebconf.sh
sed -i "s/LAUNCHER.*//g" /iexec/conf/xtremweb.server.conf

# change DBHOST value in the config if defined
if [ ! -z $DBHOST ] ; then
  sed -i "s/^DBHOST=.*/DBHOST=$DBHOST/g" /iexec/conf/xtremweb.server.conf
fi

# add the TMPDIR variable if defined
if [ ! -z $TMPDIR ] ; then
	sed -i "s/^#TMPDIR=.*/TMPDIR=$TMPDIR/g" /iexec/conf/xtremweb.server.conf
fi

# keystore is generated from the script xwhepgenkeys directly in the container
rm /iexec/keystore/cacerts
rm /iexec/keystore/*.keys
rm /iexec/keystore/*.p12
/iexec/bin/xwhepgenkeys
if [ $? -eq 0 ] ; then echo "Keystores generated" ; else echo "ERROR: keystores generation error"; exit 1 ;
fi

# change all the values defined here




/iexec/bin/xtremweb.server console
