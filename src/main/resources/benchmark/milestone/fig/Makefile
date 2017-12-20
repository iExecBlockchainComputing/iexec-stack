
CSVOPTS = -v of=csv
DATOPTS = -v of=gnuplot

ROOTDIR=..
DATADIR=$(ROOTDIR)/datas
FIGDIR=$(ROOTDIR)/fig
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

VERSUS0 : $(DATADIR)/mounts_pipeudp0.dat $(DATADIR)mounts_xw_client0.dat
	gnuplot $(SCRIPTDIR)/pipeudpVSxw.gp

$(FIGDIR)/%ps : $(DATADIR)/%gp
	$(GNUPLOT) $<
