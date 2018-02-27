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

/xwhep/bin/xtremweb.server console
