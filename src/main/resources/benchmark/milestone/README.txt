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
File   : trunc/benchmark/milstone/
Date   : 10 juillet 2008
Author : Oleg Lodygensky

This directory contains :
 - Makefile
 - README.txt
 - datas : milestone outputs
 - fig : generated graphs
 - scripts

Here are scripts that generates graphics from milestone :
  - milestone is included in XWHEP to generate time stamps
  - milestone in turned on from [server | client | worker] config file
  - milestone generates output to stdout
  - run the platform and store outputs to datas/
    * e.g. datas/server.out datas/client.out datas/worker.out
    * file extension **must** be ".out"
  - $> make
 
That's all folks

