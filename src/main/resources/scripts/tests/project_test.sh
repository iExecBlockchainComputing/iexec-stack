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

GRP1NAME="GRP1_${THEDATE}"
echo "GRP1NAME=${GRP1NAME}"

ADMING1="adming1_${THEDATE}"
ADMING1CONFFILE="${ADMING1}.conf"
${XWBINDIR}/xwsendusergroup ${GRP1NAME} ${ADMING1} ${ADMING1} ${ADMING1} --xwconfig ${XWCONFFILE} > ${ADMING1CONFFILE} || fatal "Can't add group ${GRP1NAME}"

GROUP1URI=$(head -1 ${ADMING1CONFFILE})
echo "GROUP1URI=${GROUP1URI}"
cat ${ADMING1CONFFILE} | sed "s/xw:/#xw:/g" > ${ADMING1CONFFILE}.tmp
mv ${ADMING1CONFFILE}.tmp ${ADMING1CONFFILE}

GRP2NAME="GRP2_${THEDATE}"
echo "GRP2NAME=${GRP2NAME}"
ADMING2="adming2_${THEDATE}"
ADMING2CONFFILE="${ADMING2}.conf"
${XWBINDIR}/xwsendusergroup ${GRP2NAME} ${ADMING2} ${ADMING2} ${ADMING2} --xwconfig ${XWCONFFILE} > ${ADMING2CONFFILE} || fatal "Can't add group ${GRP2NAME}"

GROUP2URI=$(head -1 ${ADMING2CONFFILE})
echo "GROUP2URI=${GROUP2URI}"
cat ${ADMING2CONFFILE} | sed "s/xw:/#xw:/g" > ${ADMING2CONFFILE}.tmp
mv ${ADMING2CONFFILE}.tmp ${ADMING2CONFFILE}

USER1="user1_${THEDATE}"
USER1CONFFILE="${USER1}.conf"
${XWBINDIR}/xwsenduser ${USER1} ${USER1} ${USER1} STANDARD_USER --xwconfig ${XWCONFFILE} > ${USER1CONFFILE} || fatal "Can't add ${USER1}"

USERG1="userg1_${THEDATE}"
USERG1CONFFILE="${USERG1}.conf"
${XWBINDIR}/xwsenduser ${USERG1} ${USERG1} ${USERG1} STANDARD_USER ${GROUP1URI} --xwconfig ${ADMING1CONFFILE} > ${USERG1CONFFILE}|| fatal "Can't add ${USERG1}"

VWORKERG1="vworkerg1_${THEDATE}"
VWORKERG1CONFFILE="${VWORKERG1}.conf"
${XWBINDIR}/xwsenduser ${VWORKERG1} ${VWORKERG1} ${VWORKERG1} VWORKER_USER ${GROUP1URI} --xwconfig ${ADMING1CONFFILE} > ${VWORKERG1CONFFILE}|| fatal "Can't add ${VWORKERG1}"
cat ${VWORKERG1CONFFILE}| sed "s/CLIENT/WORKER/g" > ${VWORKERG1CONFFILE}.tmp
mv ${VWORKERG1CONFFILE}.tmp ${VWORKERG1CONFFILE}

VWORKERG12="vworkerg12_${THEDATE}"
VWORKERG12CONFFILE="${VWORKERG12}.conf"
${XWBINDIR}/xwsenduser ${VWORKERG12} ${VWORKERG12} ${VWORKERG12} VWORKER_USER ${GROUP1URI} --xwconfig ${ADMING1CONFFILE} > ${VWORKERG12CONFFILE}|| fatal "Can't add ${VWORKERG12}"
cat ${VWORKERG12CONFFILE}| sed "s/CLIENT/WORKER/g" > ${VWORKERG12CONFFILE}.tmp
mv ${VWORKERG12CONFFILE}.tmp ${VWORKERG12CONFFILE}
echo "PROJECT=${GRP1NAME}" >> ${VWORKERG12CONFFILE}

USERG2="userg2_${THEDATE}"
USERG2CONFFILE="${USERG2}.conf"
${XWBINDIR}/xwsenduser ${USERG2} ${USERG2} ${USERG2} STANDARD_USER ${GROUP2URI} --xwconfig ${ADMING2CONFFILE} > ${USERG2CONFFILE}|| fatal "Can't add ${USERG2}"

VWORKERG2="vworkerg2_${THEDATE}"
VWORKERG2CONFFILE="${VWORKERG2}.conf"
${XWBINDIR}/xwsenduser ${VWORKERG2} ${VWORKERG2} ${VWORKERG2} VWORKER_USER ${GROUP2URI} --xwconfig ${ADMING1CONFFILE} > ${VWORKERG2CONFFILE}|| fatal "Can't add ${VWORKERG2}"
cat ${VWORKERG2CONFFILE}| sed "s/CLIENT/WORKER/g" > ${VWORKERG2CONFFILE}.tmp
mv ${VWORKERG2CONFFILE}.tmp ${VWORKERG2CONFFILE}

LSPUB="lspub_${THEDATE}"
LSGRP1="lsgrp1_${THEDATE}"
LSGRP2="lsgrp2_${THEDATE}"
${XWBINDIR}/xwsendapp ${LSPUB} deployable macosx x86_64 /bin/ls --xwconfig ${XWCONFFILE} || fatal "Can't sendapp ${LSPUB}"
${XWBINDIR}/xwsendapp ${LSGRP1} deployable macosx x86_64 /bin/ls --xwconfig ${ADMING1CONFFILE}|| fatal "Can't add ${LSGRP1}"
${XWBINDIR}/xwsendapp ${LSGRP2} deployable macosx x86_64 /bin/ls --xwconfig ${ADMING2CONFFILE}|| fatal "Can't add ${LSGRP2}"

${XWBINDIR}/xwsubmit ${LSPUB}  --xwlabel lspub_user1 --xwconfig ${USER1CONFFILE}|| fatal "User ${USER1} can't submit for ${LSPUB}"
${XWBINDIR}/xwsubmit ${LSPUB}  --xwlabel lspub_userg1 --xwconfig ${USERG1CONFFILE}|| fatal "User ${USERG1} can't submit for ${LSPUB}"
${XWBINDIR}/xwsubmit ${LSGRP1} --xwlabel lsgrp1_userg1 --xwconfig ${USERG1CONFFILE}|| fatal "User ${USERG1} can't submit for ${LSGRP1}"
${XWBINDIR}/xwsubmit ${LSPUB}  --xwlabel lspub_userg2 --xwconfig ${USERG2CONFFILE}|| fatal "User ${USERG2} can't submit for ${LSPUB}"
${XWBINDIR}/xwsubmit ${LSGRP2} --xwlabel lsgrp2_userg2 --xwconfig ${USERG2CONFFILE}|| fatal "User ${USERG2} can't submit for ${LSGRP2}"

${XWBINDIR}/xwsubmit ${LSGRP1} --xwlabel lsgrp1_user1 --xwconfig ${USER1CONFFILE} && fatal "User ${USER1} can submit ${LSGRP1} ???"
${XWBINDIR}/xwsubmit ${LSGRP2} --xwlabel lsgrp2_user1 --xwconfig ${USER1CONFFILE} && fatal "User ${USER1} can submit ${LSGRP2} ???"
${XWBINDIR}/xwsubmit ${LSGRP1} --xwlabel lsgrp1_userg2 --xwconfig ${USERG2CONFFILE} && fatal "User ${USERG2} can submit ${LSGRP1} ???"
${XWBINDIR}/xwsubmit ${LSGRP2} --xwlabel lsgrp2_user1 --xwconfig ${USERG1CONFFILE} && fatal "User ${USERG1} can submit ${LSGRP2} ???"


echo ${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG1CONFFILE}
#echo ${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG1CONFFILE}
#echo ${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG1CONFFILE}

#${XWBINDIR}/xwworks


echo ${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG12CONFFILE}
#echo ${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG12CONFFILE}
#echo ${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG12CONFFILE}

#${XWBINDIR}/xwworks

echo ${XWBINDIR}/xwworkrequest --xwconfig ${VWORKERG2CONFFILE}
