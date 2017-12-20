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
DATADIR=$(ROOTDIR)/databases
FIGDIR=$(ROOTDIR)/fig
SCRIPTDIR=$(ROOTDIR)/scripts

GNUPLOT = gnuplot

RAWS = $(wildcard *.gp)
PNGS  = $(RAWS:.gp=.png)
GPS  = $(RAWS:.gp=.gp)

all: $(PNGS)
	@echo $(RAWS) $(PNGS) done

clean :
	rm -f *.gp *.png

%.png : %.gp
	$(GNUPLOT) $<
