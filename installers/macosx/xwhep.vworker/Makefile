INST=installer
PMNAME=xwhep-vworker
PMDOC=${PMNAME}.pmdoc
PF=Resources/postflight

%:
	cp -Rf ${INST} $*
	mv $*/${PMDOC} $*/${PMNAME}-$*.pmdoc
	cat ${INST}/${PF} | sed "s/^LIVECD=.*/LIVECD=\/private\/etc\/\$$\P\K\G\/iso\/$*.iso/g" > $*/${PF}
	#packagemaker --root $* -i fr.lal.in2p3.xtremweb.vworker.$*

