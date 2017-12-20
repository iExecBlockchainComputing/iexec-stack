#!/bin/sh
#=============================================================================
# Copyrights     : CNRS
# Authors        : Oleg Lodygensky
# Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by INRIA : http://www.xtremweb.net/
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
#=============================================================================

#=============================================================================
#
# File :  spip3_towordpress41.sh
#
# Requirements   : your WordPress4.1 database name; your Spip3 database dump
#                  wordpress database must already exist
#                  spip      database must already exist
# Purpose        : this script convert Spip3 database to WordPress4.1
#                  -1- converts Spip URL ([blabla->url]) to HTML ones (<a href=\"url\">blabla</a>)
#                  -2- inserts  Spip3 dump into mysql database (Spip database must already exists)
#                  -3- converts Spip3 articles to WordPress4.1 posts
#                  -4- converts Spip3 rubriques and sous rubriques to WordPress4.1 categories and sub categories
#
# Dependency     : spip3_to_wordpress41.sql
# Usage          : spip3_to_wordpress41.sh  yourWordPressDatabaseName yourSpipDumpSqlfile [yourSpipDBName]
#
#    This is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#
# 2015-02-12  Oleg Lodygensky (lodygens A_T lal D_O_T IN2P3 D_O_T fr)
#
#=============================================================================


# --------------------------------
# error()
# --------------------------------
error ()
{
  echo "ERROR : $*"
  exit 1
}

# --------------------------------
# usage()
# --------------------------------
usage ()
{
  head -30 $0
  echo ""
  echo "Usage : $0 yourWordpressDatabaseName yourSpipDumSqlFile [SpipDBName]"
  echo "where :"
  echo "        yourWordpressDatabaseName is your Wordpress4.1 database name"
  echo "        yourSpipDumSqlFile is the SQL file containing your Spip3 database dump"
  exit 1
}

# --------------------------------
# main
# --------------------------------


[ $# -eq 0 ] && usage


WORDPRESSDBNAME=$1
[ -z "$WORDPRESSDBNAME" ] && usage
shift

ROOT=`dirname $0`
CONVERTSQLFILENAME="spip3_to_wordpress41.sql"
CONVERTSQLFILE="${ROOT}/${CONVERTSQLFILENAME}"
[ -r $CONVERTSQLFILE ]  || error "can't find $CONVERTSQLFILE"

SPIPDUMPSQLFILE=$1
shift
[ -z "$SPIPDUMPSQLFILE" ] && usage
[ -r  $SPIPDUMPSQLFILE  ] || error "can't find $SPIPDUMPSQLFILE"


SPIPDBNAME=`grep 'Database:' $SPIPDUMPSQLFILE | cut -f 3 -d ":" | sed 's/[[:space:]]*//g'`
if [ -z $SPIPDBNAME ] ; then
  echo "WARNING : Cant' find database name from $SPIPDUMPSQLFILE"
  SPIPDBNAME=$2
fi

[ -z $SPIPDBNAME ] && usage

echo "Database Name = $SPIPDBNAME"

OUTPUTDIR=`dirname $SPIPDUMPSQLFILE`
CLEAN_SPIPDUMPSQLFILENAME=`basename $SPIPDUMPSQLFILE`_${SPIPDBNAME}.sql
CLEAN_SPIPDUMPSQLFILE=${OUTPUTDIR}/${CLEAN_SPIPDUMPSQLFILENAME}
echo CLEAN_SPIPDUMPSQLFILE=$CLEAN_SPIPDUMPSQLFILE

[ -f $CLEAN_SPIPDUMPSQLFILE ] && mv -f $CLEAN_SPIPDUMPSQLFILE ${CLEAN_SPIPDUMPSQLFILE}_bak

TMPFILENAME=`basename $0`_tmp
TMPFILE=${OUTPUTDIR}/${TMPFILENAME}
echo OUTPUTDIR=$OUTPUTDIR
echo TMPFILENAME=$TMPFILENAME
echo TMPFILE=$TMPFILE

echo "Converting Spip URL ([text->url]) to HTML ones (<a href=\"url\">text</a>)"
cat $SPIPDUMPSQLFILE | sed -E 's/\[([^]]*)->([^]]*)\]/<a href=\"\2\">\1<\/a>/g' > $CLEAN_SPIPDUMPSQLFILE
[ $? -eq 0 ] || error "can't convert Spip URL"

mv $CLEAN_SPIPDUMPSQLFILE $TMPFILE
echo "Converting Spip article URL to Wordpress post ones"
cat $TMPFILE | sed -E 's/http\:\/\/www.xtremweb-hep\.org\/spip\.php\?article/?p=/g' > $CLEAN_SPIPDUMPSQLFILE
[ $? -eq 0 ] || error "can't convert Spip article URL"

mv $CLEAN_SPIPDUMPSQLFILE $TMPFILE
echo "Converting Spip rubrique URL to Wordpress category ones"
cat $TMPFILE | sed -E 's/http\:\/\/www.xtremweb-hep\.org\/spip\.php\?rubrique/?cat=/g' > $CLEAN_SPIPDUMPSQLFILE
[ $? -eq 0 ] || error "can't convert Spip rubrique URL"


CONVERTSQLFILENAME_FORDB=${CONVERTSQLFILENAME}_${SPIPDBNAME}.sql
CONVERTSQLFILE_FORDB=${OUTPUTDIR}/${CONVERTSQLFILENAME_FORDB}

cat ${CONVERTSQLFILE} | sed 's/SPIPDATABASENAME/'$SPIPDBNAME'/g' > ${CONVERTSQLFILE_FORDB}
[ $? -eq 0 ] || error "can't create ${CONVERTSQLFILE_FORDB}"


mysql -u root ${SPIPDBNAME} < $CLEAN_SPIPDUMPSQLFILE
[ $? -eq 0 ] || error "can't insert your Spip3 dump into ${SPIPDBNAME}"

mysql -u root ${WORDPRESSDBNAME} < ${CONVERTSQLFILE_FORDB}
[ $? -eq 0 ] || error "can't convert your Spip3 dump into ${WORDPRESSDBNAME} using ${CONVERTSQLFILE_FORDB}"

echo "Done"
#
# EOF
#

