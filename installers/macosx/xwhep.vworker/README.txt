
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


#  ******************************************************************
#  File    : installers/macosx/xwhep.vworker/README.txt
#  Date    : March 15th, 2012
#  Author  : Oleg Lodygensky
# 
#  OS      : Apple Mac OS X
# 
#  ******************************************************************

This directory contains files to generate an Apple Mac OS X package
for an XWHEP worker in a VirtualBox  virtual machine 

The virtualized worker is written so that more than 1 virtualized worker 
can be installed in a single physical host.

Use the make tool to create a new virtualized worker installation package.
-1- create an installer providing the new VM name (e.g. "dummy") : 
    $> make dummy
-2- copy your LiveCD
    => the name of the LiveCD **must be** theVMName.iso, here "dummy.iso"
    $> cp yourLiveCD \
       dummy/installer/PckRoot/private/etc/xwhep-vworker/iso/dummy.iso
-3- that is it!


