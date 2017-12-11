
SHELL=/bin/sh

DBNAME=xwservpub_20130624
DBUSER=root
DBPASSWD=
DBHOST=localhost
MYSQL=@mysql -B -h $(DBHOST) -u $(DBUSER) --password=$(DBPASSWD)
ECHOMYSQL=mysql -B -h $(DBHOST) -u $(DBUSER) --password=XXXXX

ROOTDIR=..
DATADIR=$(ROOTDIR)/databases
CSVDIR=$(ROOTDIR)/csv
FIGDIR=$(ROOTDIR)/fig
SCRIPTDIR=$(ROOTDIR)/scripts
GPDIR=$(ROOTDIR)/gnuplot

RAWS = $(wildcard *.sql)
CSVS = $(RAWS:.sql=.csv)
GPS  = $(RAWS:.sql=.gp)
PS0 = $(GPS:.gp=.ps)
PS = $(subst $(DATADIR), $(FIGDIR), $(PS0))

GNUPLOTSCRIPT=$(SCRIPDIR)/gnuplot.gp

.PRECIOUS: $(DATS) $(CSVS)

all: $(CSVS) $(GPS)
	@echo Databases ready

clean: $(RAWS)
	rm -f *.csv
	#@echo $(ECHOMYSQL) -e "drop database if exists $(shell basename $< .sql)"
	#$(MYSQL) -e "drop database if exists $(shell basename $< .sql)"
	cd $(SCRIPTDIR) && make -e clean
	cd $(GPDIR) && make -e clean 
	cd $(FIGDIR) && make -e clean

%.csv : %.sql
	#@echo $(ECHOMYSQL) -e "drop database if exists $*"
	#$(MYSQL) -e "drop database if exists $*"
	#@echo $(ECHOMYSQL) -e "create database $*"
	#$(MYSQL) -e "create database $*"
	#@echo $(ECHOMYSQL) $* < $< 
	#$(MYSQL) $* < $< 
	cd $(SCRIPTDIR) && make -e DBNAME=$* DBUSER="$(DBUSER)" DBPASSWD="$(DBPASSWD)" DBHOST="$(DBHOST)"
	cd $(GPDIR) && make -e DBNAME=$*
	cd $(FIGDIR) && make -e

%.gp : %.sql
	cd $(GPDIR) && make -e DBNAME=$* DBUSER="$(DBUSER)" DBPASSWD="$(DBPASSWD)" DBHOST="$(DBHOST)"
