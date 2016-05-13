#
# Copyrights     : CNRS
# Author         : Oleg Lodygensky
# Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
# Web            : http://www.xtremweb-hep.org
# 
#      This file is part of XtremWeb-HEP.
#
#    XtremWeb-HEP is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    XtremWeb-HEP is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
#
#


#!/bin/sh

ROOTDIR=`dirname $0`

cat <<EOF_CONF > $ROOTDIR/build.conf

SHELL=/bin/sh

<?xml version="1.0" encoding="UTF-8"?>

system.user=myuseraccount
xtremweb.admin.login=admin
xtremweb.admin.password=adminp
xtremweb.admin.email=
xtremweb.admin.fname=
xtremweb.admin.lname=

xtremweb.worker.login=worker
xtremweb.worker.password=workerp
xtremweb.worker.email=
xtremweb.worker.fname=
xtremweb.worker.lname=


dispatcher.servers=localhost

db.system=mysql
db.host=localhost
db.su.login=root
db.name=xtremweb

install.dir=/opt


#The dirrectory where the xtremweb php interface will be installed
mkdir -p /tmp/www/XWHEP
install.www.dir=/tmp/www/XWHEP/
ganglia.www.dir=/tmp/www/XWHEP/


xw.server.keypassword=my server pass
xw.worker.keypassword=my worker pass
xw.client.keypassword=my client pass
xw.keypassphrase=my key opass


debug=on
debuglevel=lines,source,vars
logger.level=debug

xwidl.opts="-verbose -headers -interface -java -rmi -comm -handler -file "

sonar.host.url=http://localhost:9000/

EOF_CONF

./build.sh
