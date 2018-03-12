#!/bin/bash

# Replace DBUSER in the script
if [ ! -z $DBUSER ] ; then
	sed -i "s/my \$DBUSER = .*/my \$DBUSER = \"$DBUSER\";/g" xtremweb.gmond.pl
fi

# Replace DBPASSWORD in the script
if [ ! -z $DBPASSWORD ] ; then
	sed -i "s/my \$dbpassword = .*/my \$dbpassword = \"$DBPASSWORD\";/g" xtremweb.gmond.pl
fi

# Replace DBHOST in the script
if [ ! -z $DBHOST ] ; then
	sed -i "s/my \$DBHOST = .*/my \$DBHOST = \"$DBHOST\";/g" xtremweb.gmond.pl
fi

# Replace database in the script
if [ ! -z $DATABASE ] ; then
	sed -i "s/my \$database = .*/my \$database = \"$DATABASE\";/g" xtremweb.gmond.pl
fi

perl xtremweb.gmond.pl
