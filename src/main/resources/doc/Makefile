PROJECT=xwhep
TEXI=$(PROJECT).texi
HTML=$(PROJECT).html
DVI=$(PROJECT).dvi
PDF=$(PROJECT).pdf
PS=$(PROJECT).ps

TEXI2HTML=texi2html
TEXI2DVI=texi2dvi
DVIPDF=dvipdf
DVIPS=dvips

all : $(HTML) $(PDF) $(PS)
	echo done

$(HTML) : $(TEXI)
	$(TEXI2HTML) $<

$(PS) : $(DVI)
	$(DVIPS) $<

$(PDF) : $(DVI)
	$(DVIPDF) $<

$(DVI) : $(TEXI)
	$(TEXI2DVI) $<

clean:
	rm -f $(HTML) $(DVI) $(PDF) *.toc *.vr *.aux *.cp *.op *.ky *.pg *.tp *.cps *.fn *.log

