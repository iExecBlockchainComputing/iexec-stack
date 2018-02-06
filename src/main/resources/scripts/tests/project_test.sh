#/bin/bash

#=============================================================================
#
#  Function  fatal (Message, Force)
#
#=============================================================================
fatal ()
{
  msg="$1"

  echo  "$(date "$DATE_FORMAT")  $SCRIPTNAME  FATAL : $msg"

  ( [ "$VERBOSE" ]  &&  set -x
    "$VBMGT"  controlvm  "$VMNAME"  poweroff  > /dev/null 2>&1 )
  #
  # Inside 'fatal', the VM state is unknown and possibly inconsistent.
  # So, the above 'poweroff' request does NOT make much sense.

  exit 1
}

#=============================================================================
#
#  Function  usage ()
#
#=============================================================================
usage()
{
cat << END_OF_USAGE
  This script aims to test project management
  Please use -v for debug mode
END_OF_USAGE

  exit 0
}


while [ $# -gt 0 ]; do

  case "$1" in

    --help )
      usage
      ;;

    -v | --verbose | --debug )
      VERBOSE=1
      set -x
      ;;
  esac

  shift

done

THEDATE=$(date +"%Y%m%d_%H%M%S")

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
${XWBINDIR}/xwsendusergroup ${GRPNAME} ${ADMING1} ${ADMING1} ${ADMING1} --xwconfig ${XWCONFFILE} > ${ADMING1CONFFILE} || fatal "Can't add group ${GRPNAME}"

GROUPURI=$(head -1 ${ADMING1CONFFILE})
echo "GROUPURI=${GROUPURI}"
cat ${ADMING1CONFFILE} | sed "s/xw:/#xw:/g" > ${ADMING1CONFFILE}.tmp
mv ${ADMING1CONFFILE}.tmp ${ADMING1CONFFILE}

USER1="user1_${THEDATE}"
USER1CONFFILE="${USER1}.conf"
${XWBINDIR}/xwsenduser ${USER1} ${USER1} ${USER1} STANDARD_USER --xwconfig ${XWCONFFILE} > ${USER1CONFFILE} || fatal "Can't add ${USER1}"

USERG1="userg1_${THEDATE}"
USERG1CONFFILE="${USERG1}.conf"
${XWBINDIR}/xwsenduser ${USERG1} ${USERG1} ${USERG1} STANDARD_USER ${GROUPURI} --xwconfig ${ADMING1CONFFILE} > ${USERG1CONFFILE}|| fatal "Can't add ${USERG1}"

VWORKERG1="vworkerg1_${THEDATE}"
VWORKERG1CONFFILE="${VWORKERG1}.conf"
${XWBINDIR}/xwsenduser ${VWORKERG1} ${VWORKERG1} ${VWORKERG1} VWORKER_USER ${GROUPURI} --xwconfig ${ADMING1CONFFILE} > ${VWORKERG1CONFFILE}|| fatal "Can't add ${VWORKERG1}"
cat ${VWORKERG1CONFFILE}| sed "s/CLIENT/WORKER/g" > ${VWORKERG1CONFFILE}.tmp
mv ${VWORKERG1CONFFILE}.tmp ${VWORKERG1CONFFILE}

LSPUB="lspub_${THEDATE}"
LSGRP="lsgrp_${THEDATE}"
${XWBINDIR}/xwsendapp ${LSPUB} deployable macosx x86_64 /bin/ls --xwconfig ${XWCONFFILE} || fatal "Can't sendapp ${LSPUB}"
${XWBINDIR}/xwsendapp ${LSGRP} deployable macosx x86_64 /bin/ls --xwconfig ${ADMING1CONFFILE}|| fatal "Can't add ${LSGRP}"

${XWBINDIR}/xwsubmit ${LSPUB} --xwlabel lspub_user1 --xwconfig ${USER1CONFFILE}|| fatal "Can't add submit for ${LSPUB}"
${XWBINDIR}/xwsubmit ${LSPUB} --xwlabel lspub_userg1 --xwconfig ${USERG1CONFFILE}|| fatal "Can't add ${LSPUB}"
${XWBINDIR}/xwsubmit ${LSGRP} --xwlabel lsgrp_userg1 --xwconfig ${USERG1CONFFILE}|| fatal "Can't add ${LSGRP}"

${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG1CONFFILE}
${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG1CONFFILE}
${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG1CONFFILE}

${XWBINDIR}/xwworks
 
VWORKERG12="vworkerg12_${THEDATE}"
VWORKERG12CONFFILE="${VWORKERG12}.conf"
cp ${VWORKERG1CONFFILE} ${VWORKERG12CONFFILE}
echo "PROJECT=\"${GRPNAME}\"" >> ${VWORKERG12CONFFILE}

${XWBINDIR}/xwsubmit ${LSPUB} --xwlabel lspub_user1 --xwconfig ${USER1CONFFILE}|| fatal "Can't add submit for ${LSPUB}"
${XWBINDIR}/xwsubmit ${LSPUB} --xwlabel lspub_userg1 --xwconfig ${USERG1CONFFILE}|| fatal "Can't add ${LSPUB}"
${XWBINDIR}/xwsubmit ${LSGRP} --xwlabel lsgrp_userg1 --xwconfig ${USERG1CONFFILE}|| fatal "Can't add ${LSGRP}"

${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG12CONFFILE}
${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG12CONFFILE}
${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG12CONFFILE}

${XWBINDIR}/xwworks
