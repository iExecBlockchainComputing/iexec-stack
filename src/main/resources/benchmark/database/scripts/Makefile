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

MYSQL=@mysql -B -h ${DBHOST} -u $(DBUSER) --password=$(DBPASSWD)
ECHOMYSQL=mysql -B -h $(DBHOST) -u $(DBUSER) --password=XXXXXX

RAWS = $(wildcard *.sql)
CSVS = $(RAWS:.sql=.csv)

GNUPLOTSCRIPT=$(SCRIPDIR)/gnuplot.gp

.PRECIOUS: $(DATS) $(CSVS)

all: $(CSVS)
	@echo $(CSVS) $(SQLS)
	@echo $(DBNAME) : done

%csv : %sql
	@echo "$(ECHOMYSQL) < $< > $(CSVDIR)/$(DBNAME)_$@"
	$(MYSQL) $(DBNAME)  < $< > $(CSVDIR)/$(DBNAME)_$@
	@if [ ! -s $(CSVDIR)/$(DBNAME)_$@ ]; then rm -f $(CSVDIR)/$(DBNAME)_$@ ; echo canceling $(CSVDIR)/$(DBNAME)_$@ ; fi

clean :
	rm -f *~ *dat *csv  $(CSVDIR)/*
