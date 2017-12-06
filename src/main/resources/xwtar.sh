
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


SHELL=/bin/sh


#!/bin/sh

ROOT=`dirname $0`
if [ "$ROOT" = '.' ] ; then
  ROOT=`pwd`
fi

( cd $ROOT && make clean )

CD=`pwd`
cd $ROOT/..
VERSION=`pwd | xargs basename`
cd $CD

TARFILENAME=XWHEP-$VERSION.tar.bz2
TMPTARFILE=/tmp/$TARFILENAME

rm -f $ROOT/$TARFILENAME
(cd $ROOT/../../ && rm -f $TMPTARFILE && tar cXvfj $ROOT/xwtarexclude.txt $TMPTARFILE $VERSION && mv $TMPTARFILE $ROOT/ )
