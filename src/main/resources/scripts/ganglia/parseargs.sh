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
#


# Author: Oleg Lodygensky
# E-mail: lodygens@lal.in2p3.fr
#
# File   : parseargs.sh
#


# ------------------- Local Function

Help()
{ 
	echo
	echo $0 [-h] [-u user] [-p passwd] [-d database] [-f sqlfile]
	echo "	-h : get this help msg"
	echo "	-u : db user"
	echo "	-p : user password"
	echo "	-f : sql command file"
	echo "	-d : db name"
	echo
 }

# ------------------- Main Function

#USER=""
#PASSWORD=""
#DATABASE="xtremweb"
SQLFILE=""

while [ $# -ne 0 ]; do

  case $1 in
    "-h" | "--help" ) 
      Help
      exit
    ;; 
    "-u" )
    shift
    USER=$1
    ;;
    "-p" )
    shift
    PASSWORD=$1
    ;;
    "-d" )
    shift
    DATABASE=$1
    ;;
    "-f" )
    shift
    SQLFILE=$1
    ;;
  * )
    echo "Unknown args : $1"
    ;; 
  esac

  shift

done


