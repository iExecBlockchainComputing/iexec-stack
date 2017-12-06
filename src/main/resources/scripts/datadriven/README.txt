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



#
# src/scripts/datadriven: 
#

This directory contains four scripts to test data driven mechanism:
-1- install_and_start_datadriven_server.sh
-2- install_and_start_datadriven_workers.sh
-3- submit_datadriven.sh
-4- monitor_datadriven.sh


All parts (server, client, workers) are started on the same machine (localhost).

Prerequisites:
- the XWHEP binary package must be ready (you must have run bin/xwconfigure)
- the database must be ready
- there must be a registered application named "ls"
- there must be a registered application named "apptest"


-1- install_and_start_datadriven_server.sh
  This script starts the server

-2- install_and_start_datadriven_workers.sh
  This script configures workers; copies, configure and start monitor_datadriven.sh

-3- submit_datadriven.sh
  This script submits two jobs, each is 1000 times replicated
  
-4- monitor_datadriven.sh


