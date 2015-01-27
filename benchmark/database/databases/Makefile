
SHELL=/bin/sh

DBNAME=xwservpub_20130624
DBUSER=root
DBPASSWD=
DBHOST=localhost
MYSQL=@mysql -B -h $(DBHOST) -u $(DBUSER) --password=$(DBPASSWD)
ECHOMYSQL=mysql -B -h $(DBHOST) -u $(DBUSER) --password=XXXXXX

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
	cd $(SCRIPTDIR) && make clean
	cd $(GPDIR) && make clean 
	cd $(FIGDIR) && make clean

%.csv : %.sql
	#@echo $(ECHOMYSQL) -e "drop database if exists $*"
	#$(MYSQL) -e "drop database if exists $*"
	#@echo $(ECHOMYSQL) -e "create database $*"
	#$(MYSQL) -e "create database $*"
	#@echo $(ECHOMYSQL) $* < $< 
	#$(MYSQL) $* < $< 
	cd $(SCRIPTDIR) && make DBNAME=$* DBUSER="$(DBUSER)" DBPASSWD="$(DBPASSWD)" DBHOST="$(DBHOST)"
	cd $(GPDIR) && make DBNAME=$*
	cd $(FIGDIR) && make

%.gp : %.sql
	cd $(GPDIR) && make DBNAME=$* DBUSER="$(DBUSER)" DBPASSWD="$(DBPASSWD)" DBHOST="$(DBHOST)"
