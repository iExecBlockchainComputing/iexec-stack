#/bin/bash

THEDATE=$(date +"%s")

progdir=`dirname $0`

currentDir=`pwd`
cd $progdir/../..
ROOTDIR=`pwd`
cd $currentDir

XWBINDIR=${ROOTDIR}/bin
XWCONFDIR=${ROOTDIR}/conf
XWCONFFILE=${XWCONFDIR}/xtremweb.client.conf
XWKEYDIR=${ROOTDIR}/keystore

GRPNAME="GRP_${THEDATE}"
echo "GRPNAME=${GRPNAME}"

ADMING1="adming1_${THEDATE}"
ADMING1CONFFILE="${ADMING1}.conf"
${XWBINDIR}/xwsendusergroup ${GRPNAME} ${ADMING1} ${ADMING1} ${ADMING1} --xwconfig ${XWCONFFILE} > ${ADMING1CONFFILE}

GROUPURI=$(head -1 ${ADMING1CONFFILE})
echo "GROUPURI=${GROUPURI}"
cat ${ADMING1CONFFILE} | sed "s/xw:/#xw:/g" > ${ADMING1CONFFILE}.tmp
mv ${ADMING1CONFFILE}.tmp ${ADMING1CONFFILE}

USER1="user1_${THEDATE}"
USER1CONFFILE="${USER1}.conf"
${XWBINDIR}/xwsenduser ${USER1} ${USER1} ${USER1} STANDARD_USER --xwconfig ${XWCONFFILE} > ${USER1CONFFILE}

USERG1="userg1_${THEDATE}"
USERG1CONFFILE="${USERG1}.conf"
${XWBINDIR}/xwsenduser ${USERG1} ${USERG1} ${USERG1} STANDARD_USER ${GROUPURI} --xwconfig ${ADMING1CONFFILE} > ${USERG1CONFFILE}

VWORKERG1="vworkerg11_${THEDATE}"
VWORKERG1CONFFILE="${VWORKERG1}.conf"
${XWBINDIR}/xwsenduser ${VWORKERG1} ${VWORKERG1} ${VWORKERG1} VWORKER_USER ${GROUPURI} --xwconfig ${ADMING1CONFFILE} > ${VWORKERG1CONFFILE}
cat ${VWORKERG1CONFFILE}| sed "s/CLIENT/WORKER/g" > ${VWORKERG1CONFFILE}.tmp
mv ${VWORKERG1CONFFILE}.tmp ${VWORKERG1CONFFILE}

LSPUB="lspub_${THEDATE}"
LSGRP="lsgrp_${THEDATE}"
${XWBINDIR}/xwsendapp ${LSPUB} deployable macosx x86_64 /bin/ls --xwconfig ${XWCONFFILE}
${XWBINDIR}/xwsendapp ${LSGRP} deployable macosx x86_64 /bin/ls --xwconfig ${ADMING1CONFFILE}

${XWBINDIR}/xwsubmit ${LSPUB} --xwlabel lspub_user1 --xwconfig ${USER1CONFFILE}
${XWBINDIR}/xwsubmit ${LSPUB} --xwlabel lspub_userg1 --xwconfig ${USERG1CONFFILE}
${XWBINDIR}/xwsubmit ${LSGRP} --xwlabel lsgrp_userg1 --xwconfig ${USERG1CONFFILE}

${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG1CONFFILE}
${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG1CONFFILE}
${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG1CONFFILE}

${XWBINDIR}/xwworks
 
