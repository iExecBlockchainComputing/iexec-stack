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

CSVOPTS = -v of=csv
DATOPTS = -v of=gnuplot

ROOTDIR=..
DATADIR=$(ROOTDIR)/final3cleaned
FIGDIR=$(ROOTDIR)/figfinal3cleaned
SCRIPTDIR=$(ROOTDIR)/scripts

AWK = gawk
AWKSCRIPT = $(SCRIPTDIR)/parse.awk
AWKOPTS = -f $(AWKSCRIPT)

GNUPLOT = gnuplot

PS = mounts.ps
JPEG = mounts.jpeg

GPS = $(wildcard $(DATADIR)/*.gp)
PS0 = $(GPS:.gp=.ps)
PS = $(subst $(DATADIR), $(FIGDIR), $(PS0))

all: $(PS)

rows:
	gnuplot < $(DATADIR)/exp0X_dispatchers.gplot
	gnuplot < $(DATADIR)/exp1X_dispatchers.gplot
	gnuplot < $(DATADIR)/exp2X_dispatchers.gplot
	gnuplot < $(DATADIR)/exp3X_dispatchers.gplot
	gnuplot < $(DATADIR)/exp4X_dispatchers.gplot
	gnuplot < $(DATADIR)/exp5X_dispatchers.gplot
	gnuplot < $(DATADIR)/exp6X_dispatchers.gplot
	gnuplot < $(DATADIR)/exp7X_dispatchers.gplot
	gnuplot < $(DATADIR)/exp8X_dispatchers.gplot
	gnuplot < $(DATADIR)/exp0X_clients.gplot
	gnuplot < $(DATADIR)/exp1X_clients.gplot
	gnuplot < $(DATADIR)/exp2X_clients.gplot
	gnuplot < $(DATADIR)/exp3X_clients.gplot
	gnuplot < $(DATADIR)/exp4X_clients.gplot
	gnuplot < $(DATADIR)/exp5X_clients.gplot
	gnuplot < $(DATADIR)/exp6X_clients.gplot
	gnuplot < $(DATADIR)/exp7X_clients.gplot
	gnuplot < $(DATADIR)/exp8X_clients.gplot
	gnuplot < $(DATADIR)/exp0X_workers.gplot
	gnuplot < $(DATADIR)/exp8X_workers.gplot
	gnuplot < $(DATADIR)/exp1X_workers.gplot
	gnuplot < $(DATADIR)/exp2X_workers.gplot
	gnuplot < $(DATADIR)/exp3X_workers.gplot
	gnuplot < $(DATADIR)/exp4X_workers.gplot
	gnuplot < $(DATADIR)/exp5X_workers.gplot
	gnuplot < $(DATADIR)/exp6X_workers.gplot
	gnuplot < $(DATADIR)/exp7X_workers.gplot
	gnuplot < $(DATADIR)/expX2_workers.gplot
	gnuplot < $(DATADIR)/expX3_workers.gplot
	gnuplot < $(DATADIR)/expX4_workers.gplot
	gnuplot < $(DATADIR)/expX2_dispatchers.gplot
	gnuplot < $(DATADIR)/expX3_dispatchers.gplot
	gnuplot < $(DATADIR)/expX4_dispatchers.gplot
	gnuplot < $(DATADIR)/expX2_clients.gplot
	gnuplot < $(DATADIR)/expX3_clients.gplot
	gnuplot < $(DATADIR)/expX4_clients.gplot

events:
	gnuplot < $(DATADIR)/resultats.gplot
	gnuplot < $(DATADIR)/exp0X_workers_workRequest@done.gplot
	gnuplot < $(DATADIR)/exp0X_workers_networkRead.gplot
	gnuplot < $(DATADIR)/exp0X_workers_networkWrite.gplot
	gnuplot < $(DATADIR)/exp1X_workers_workRequest@done.gplot
	gnuplot < $(DATADIR)/exp1X_workers_networkRead.gplot
	gnuplot < $(DATADIR)/exp1X_workers_networkWrite.gplot
	gnuplot < $(DATADIR)/exp2X_workers_workRequest@done.gplot
	gnuplot < $(DATADIR)/exp2X_workers_networkRead.gplot
	gnuplot < $(DATADIR)/exp2X_workers_MOUNTPROC_MNT@waiting@result.gplot
	gnuplot < $(DATADIR)/exp2X_workers_networkWrite.gplot
	gnuplot < $(DATADIR)/exp3X_workers_workRequest@done.gplot
	gnuplot < $(DATADIR)/exp3X_workers_networkRead.gplot
	gnuplot < $(DATADIR)/exp3X_workers_networkWrite.gplot
	gnuplot < $(DATADIR)/exp4X_workers_workRequest@done.gplot
	gnuplot < $(DATADIR)/exp4X_workers_networkRead.gplot
	gnuplot < $(DATADIR)/exp4X_workers_networkWrite.gplot
	gnuplot < $(DATADIR)/exp5X_workers_workRequest@done.gplot
	gnuplot < $(DATADIR)/exp5X_workers_networkRead.gplot
	gnuplot < $(DATADIR)/exp5X_workers_networkWrite.gplot
	gnuplot < $(DATADIR)/exp6X_workers_workRequest@done.gplot
	gnuplot < $(DATADIR)/exp6X_workers_networkRead.gplot
	gnuplot < $(DATADIR)/exp6X_workers_networkWrite.gplot
	gnuplot < $(DATADIR)/exp7X_workers_workRequest@done.gplot
	gnuplot < $(DATADIR)/exp7X_workers_networkRead.gplot
	gnuplot < $(DATADIR)/exp7X_workers_networkWrite.gplot
	gnuplot < $(DATADIR)/exp8X_workers_workRequest@done.gplot
	gnuplot < $(DATADIR)/exp8X_workers_networkRead.gplot
	gnuplot < $(DATADIR)/exp8X_workers_networkWrite.gplot
	gnuplot < $(DATADIR)/exp8X_workers_MOUNTPROC_MNT@waiting@result.gplot
	gnuplot < $(DATADIR)/dispatchers_network.gplot
	gnuplot < $(DATADIR)/clients_network.gplot
	gnuplot < $(DATADIR)/exp0X_dispatchers_diskAccess.gplot
	gnuplot < $(DATADIR)/exp1X_dispatchers_diskAccess.gplot
	gnuplot < $(DATADIR)/exp2X_dispatchers_diskAccess.gplot
	gnuplot < $(DATADIR)/exp3X_dispatchers_diskAccess.gplot
	gnuplot < $(DATADIR)/exp4X_dispatchers_diskAccess.gplot
	gnuplot < $(DATADIR)/exp5X_dispatchers_diskAccess.gplot
	gnuplot < $(DATADIR)/exp6X_dispatchers_diskAccess.gplot
	gnuplot < $(DATADIR)/exp7X_dispatchers_diskAccess.gplot
	gnuplot < $(DATADIR)/exp8X_dispatchers_diskAccess.gplot
	gnuplot < $(DATADIR)/mysqlVShsqldb.gplot
	gnuplot < $(DATADIR)/workRequest@done.gp
	gnuplot < $(DATADIR)/workers_networkRead.gplot
	gnuplot < $(DATADIR)/workers_networkWrite.gplot
	gnuplot < $(DATADIR)/result@set.gp

PS: $(GPS)
	@echo PS done

clean :
	rm -f *client*.png
	rm -f *client*.jpeg
	rm -f *client*.ps
	rm -f *dispatcher*.png
	rm -f *dispatcher*.jpeg
	rm -f *dispatcher*.ps
	rm -f *worker*.png
	rm -f *worker*.jpeg
	rm -f *worker*.ps
	rm -f $(GPS) dispatchers.gp clients.gp workers.gp
	rm -f *ps
	rm -f *png

VERSUS0 : $(DATADIR)/mounts_pipeudp0.dat $(DATADIR)mounts_xw_client0.dat
	gnuplot $(SCRIPTDIR)/pipeudpVSxw.gp

$(FIGDIR)/%ps : $(DATADIR)/%gp
	$(GNUPLOT) $<
