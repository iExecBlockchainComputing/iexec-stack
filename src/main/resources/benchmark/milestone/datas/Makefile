
SHELL=/bin/sh

CSVOPTS = -v of=csv
DATOPTS = -v of=gnuplot

ROOTDIR=..
FIGDIR=$(ROOTDIR)/fig
SCRIPTDIR=$(ROOTDIR)/scripts

AWK = gawk
AWKSCRIPT = $(SCRIPTDIR)/parse.awk
AWKOPTS = -f $(AWKSCRIPT)

RAWS = $(wildcard *.out)
DATS = $(RAWS:.out=.dat)
CSVS = $(RAWS:.out=.csv)
GPS  = $(RAWS:.out=.gp)
PS   = $(RAWS:.out=.ps)

GNUPLOTSCRIPT=$(SCRIPDIR)/gnuplot.gp

.PRECIOUS: $(DATS) $(CSVS)

all: $(GPS)
	@echo done

%gp : %dat %csv
	#$(shell cat ../scripts/gnuplot.gp | sed "s/TITLE/`cat $< | grep TITLE | cut -d ':' -f 2`/" > $@)
	cat ../scripts/gnuplot.gp | sed "s/TITLE/`cat $< | grep TITLE | cut -d ':' -f 2`/" > $@
	@echo 01 $< $@ $*
	ls -l $@
	#$(shell cat $@ | sed "s/FILEIN/\.\.\/datas\/$*/g" > $@)
	mv $@ gptmp_file
	cat gptmp_file | sed "s/FILEIN/\.\.\/datas\/$*/g" > $@
	@echo 02 $< $@ $*
	ls -l $@
	rm -f gptmp_file
	mv $@ gptmp_file
	cat gptmp_file | sed "s/FILEIN/\.\.\/datas\/$*/g" > $@
	#$(shell cat $@ | sed "s/FILEOUT/\.\.\/fig\/$*/g" > $@)
	rm -f gptmp_file
	mv $@ gptmp_file
	cat gptmp_file | sed "s/FILEOUT/\.\.\/fig\/$*/g" > $@
	rm -f gptmp_file

%dat : %out
	$(AWK) $(AWKOPTS) $(DATOPTS) $< > $@

%csv : %out
	$(AWK) $(AWKOPTS) $(CSVOPTS) $< > $@

clean :
	rm -f *~ *dat *csv
cp:
	cp /opt/XtremWeb/rpcxw/bin/*.out .
	scp auger2:/opt/XtremWeb/rpcxw/bin/*.out .
	scp xw2:/opt/XtremWeb/rpcxw/bin/*.out .
