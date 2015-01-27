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

CLASSPATH="/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/src:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/build/classes:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/MinML.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/activation.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/antlr.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/hsqldb.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/jcert.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/jetty-6.1.1.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/jetty-util-6.1.1.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/jnet.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/jnlp.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/jsse.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/log4j.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/mm.mysql-2.0.2-bin.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/openxml-1.2.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/pop3.jar:/Users/oleg/DGHEP/1.0.0/XWHEP-1.0.0/classes/servlet-api-2.5-6.1.1.jar"

javac -cp $CLASSPATH $*
