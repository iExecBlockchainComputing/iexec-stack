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

ROOTDIR=..
FIGDIR=$(ROOTDIR)/fig
CSVDIR=$(ROOTDIR)/csv
SCRIPTDIR=$(ROOTDIR)/scripts
GPDIR=$(ROOTDIR)/gnuplot

RAWS = $(wildcard *.gp.in)
GPS  = $(RAWS:.gp.in=.gp)

GNUPLOTSCRIPT=$(SCRIPDIR)/gnuplot.gp

.PRECIOUS: $(DATS) $(CSVS)

all: $(GPS)
	@echo $(CSVS) $(SQLS)
	@echo $(DBNAME) : done

%.gp : %.gp.in
	sed "s/TITLE/$(DBNAME)/" $< > $@
	mv $@ gptmp_file
	cat gptmp_file | sed "s/FILEIN/\.\.\/csv\/$(DBNAME)/g" > $@
	mv $@ gptmp_file
	cat gptmp_file | sed "s/FILEOUT/\.\.\/fig\/$(DBNAME)/g" > $@
	rm -f gptmp_file
	mv $@ ../fig/$(DBNAME)_$@
	echo $(DBNAME) $< $@ $*
	@if [ "$*" != "summary" -a ! -s $(CSVDIR)/$(DBNAME)_$*.csv ]; then rm -f ../fig/$(DBNAME)_$@ ; echo canceling ../fig//$(DBNAME)_$@ ; fi

clean :
	rm -f ../fig/*gp
