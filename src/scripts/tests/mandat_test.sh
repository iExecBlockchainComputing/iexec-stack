#!/bin/bash

progdir=`dirname $0`

currentDir=`pwd`
cd $progdir/../..
ROOTDIR=`pwd`
cd $currentDir

XWBINDIR=${ROOTDIR}/bin
XWCONFDIR=${ROOTDIR}/conf
XWKEYDIR=${ROOTDIR}/keystore

echo "Send user worker"
${XWBINDIR}/xwsenduser worker workerp worker WORKER_USER > worker.conf
echo "Send user vworker"
${XWBINDIR}/xwsenduser vworker vworkerp vworker VWORKER_USER > vworker.conf
echo "Send user user1"
${XWBINDIR}/xwsenduser user1 user1 user1 STANDARD_USER > user1.conf
echo "Send user user2"
${XWBINDIR}/xwsenduser user2 user2 user2 STANDARD_USER > user2.conf
echo "Send user mandat"
${XWBINDIR}/xwsenduser mandat mandat mandat MANDATED_USER > mandat.conf

echo "Admin sends public application ('ls_pub')"
${XWBINDIR}/xwsendapp ls_pub deployable macosx x86_64 /bin/ls > /dev/null
echo "User1 sends private application ('ls_priv_user1')"
${XWBINDIR}/xwsendapp ls_priv_user1 deployable macosx x86_64 /bin/ls --xwconfig user1.conf > /dev/null
echo "User1 sends private application with sticky bit ('ls_priv_stickybit_user1')"
${XWBINDIR}/xwsendapp ls_priv_stickybit_user1 deployable macosx x86_64 /bin/ls --xwconfig user1.conf > /dev/null
${XWBINDIR}/xwapps ls_priv_stickybit_user1 --xwformat xml --xwconfig user1.conf | grep '<app>' > ls_priv_stickybit_user1.xml
cat ls_priv_stickybit_user1.xml | sed 's/\<accessrights\>0x700\<\/accessrights\>/\<accessrights\>0x1700\<\/accessrights\>/g' > ls_priv_stickybit_user1.tmp
mv ls_priv_stickybit_user1.tmp ls_priv_stickybit_user1.xml
${XWBINDIR}/xwsendapp  --xwconfig user1.conf --xwxml ls_priv_stickybit_user1.xml > /dev/null
echo "User2 sends private application ('ls_priv_user2')"
${XWBINDIR}/xwsendapp ls_priv_user2 deployable macosx x86_64 /bin/ls --xwconfig user2.conf > /dev/null

cat ${XWCONFDIR}/xtremweb.client.conf | grep -vi "SSLKEYSTORE" > admin.conf
echo "SSLKEYSTORE=${XWKEYDIR}/xwhepclient.keys" >> admin.conf
MANDATEDUSER="mandat"

USERS=(admin user1 user2 mandat)

for u in ${USERS[*]} ; do 
	USER=${u}
	FLIST="apps_${USER}.list"
	echo ""
	echo "Accessing apps for user ${USER}"
	echo ""
	${XWBINDIR}/xwapps --xwconfig ${USER}.conf> ${FLIST}
	(cat ${FLIST} | grep ls_pub > /dev/null ) && echo "  [SUCCESS]  : ${USER} accesses ls_pub" || echo "  [ERROR]  : ${USER} can't access ls_pub"
	(cat ${FLIST} | grep ls_priv_user1 > /dev/null ) && echo "  [SUCCESS]  : ${USER} accesses ls_priv_user1" || echo "  [ERROR]  : ${USER} can't access ls_priv_user1"
	(cat ${FLIST} | grep ls_priv_user2 > /dev/null ) && echo "  [SUCCESS]  : ${USER} accesses ls_priv_user2" || echo "  [ERROR]  : ${USER} can't access ls_priv_user2"
	(cat ${FLIST} | grep ls_priv_stickybit_user1 > /dev/null ) && echo "  [SUCCESS]  : ${USER} accesses ls_priv_stickybit_user1" || echo "  [ERROR]  : ${USER} can't access ls_priv_stickybit_user1"

done

USERS=(admin user1 user2)
for u in ${USERS[*]} ; do 
	USER=${u}
	FLIST="apps_${USER}_mandated_${MANDATEDUSER}.list"
	echo ""
	echo "Accessing apps for user ${USER} mandating ${MANDATEDUSER}"
	echo ""
	${XWBINDIR}/xwapps --xwconfig ${MANDATEDUSER}.conf -DMANDATINGUSER=${USER}> ${FLIST}
	(cat ${FLIST} | grep ls_pub > /dev/null ) && echo "  [SUCCESS]  : ${USER} accesses ls_pub, mandating ${MANDATEDUSER}" || echo "  [ERROR]  : ${USER} can't access ls_pub, mandating ${MANDATEDUSER}"
	(cat ${FLIST} | grep ls_priv_user1 > /dev/null ) && echo "  [SUCCESS]  : ${USER} accesses ls_priv_user1, mandating ${MANDATEDUSER}" || echo "  [ERROR]  : ${USER} can't access ls_priv_user1, mandating ${MANDATEDUSER}"
	(cat ${FLIST} | grep ls_priv_user2 > /dev/null ) && echo "  [SUCCESS]  : ${USER} accesses ls_priv_user2, mandating ${MANDATEDUSER}" || echo "  [ERROR]  : ${USER} can't access ls_priv_user2, mandating ${MANDATEDUSER}"
	(cat ${FLIST} | grep ls_priv_stickybit_user1 > /dev/null ) && echo "  [SUCCESS]  : ${USER} accesses ls_priv_stickybit_user1, mandating ${MANDATEDUSER}" || echo "  [ERROR]  : ${USER} can't access ls_priv_stickybit_user1, mandating ${MANDATEDUSER}"
done

USERS=(admin user1 user2 mandat)
for u in ${USERS[*]} ; do 
	USER=${u}
	FLIST="apps_${USER}_mandated_${MANDATEDUSER}.list"
	echo ""
	echo "Job submission by ${USER}"
	echo ""
	${XWBINDIR}/xwsubmit --xwconfig ${USER}.conf ls_pub                   --xwlabel ${USER}_ls_pub > /dev/null                  && echo "  [SUCCESS]  : ${USER} can submit for ls_pub" || echo "  [ERROR]  : ${USER} can't submit for ls_pub"
	${XWBINDIR}/xwsubmit --xwconfig ${USER}.conf ls_priv_user1            --xwlabel ${USER}_ls_priv_user1 > /dev/null           && echo "  [SUCCESS]  : ${USER} can submit for ls_priv_user1" || echo "  [ERROR]  : ${USER} can't submit for ls_priv_user1"
	${XWBINDIR}/xwsubmit --xwconfig ${USER}.conf ls_priv_user2            --xwlabel ${USER}_ls_priv_user2 > /dev/null           && echo "  [SUCCESS]  : ${USER} can submit for ls_priv_user2" || echo "  [ERROR]  : ${USER} can't submit for ls_priv_user2"
	${XWBINDIR}/xwsubmit --xwconfig ${USER}.conf ls_priv_stickybit_user1  --xwlabel ${USER}_ls_priv_stickybit_user1 > /dev/null && echo "  [SUCCESS]  : ${USER} can submit for ls_priv_stickybit_user1" || echo "  [ERROR]  : ${USER} can't submit for ls_priv_stickybit_user1"
done


USERS=(admin user1 user2)
for u in ${USERS[*]} ; do 
	USER=${u}
	FLIST="apps_${USER}_mandated_${MANDATEDUSER}.list"
	echo ""
	echo "Job submission by ${USER} mandating ${MANDATEDUSER}"
	echo ""
	${XWBINDIR}/xwsubmit --xwconfig ${MANDATEDUSER}.conf -DMANDATINGUSER=${USER} ls_pub                   --xwlabel ${USER}_ls_pub > /dev/null                  && echo "  [SUCCESS]  : ${USER} can submit for ls_pub, mandating ${MANDATEDUSER}" || echo "  [ERROR]  : ${USER} can't submit for ls_pub, mandating ${MANDATEDUSER}"
	${XWBINDIR}/xwsubmit --xwconfig ${MANDATEDUSER}.conf -DMANDATINGUSER=${USER} ls_priv_user1            --xwlabel ${USER}_ls_priv_user1 > /dev/null           && echo "  [SUCCESS]  : ${USER} can submit for ls_priv_user1, mandating ${MANDATEDUSER}" || echo "  [ERROR]  : ${USER} can't submit for ls_priv_user1, mandating ${MANDATEDUSER}"
	${XWBINDIR}/xwsubmit --xwconfig ${MANDATEDUSER}.conf -DMANDATINGUSER=${USER} ls_priv_user2            --xwlabel ${USER}_ls_priv_user2 > /dev/null           && echo "  [SUCCESS]  : ${USER} can submit for ls_priv_user2, mandating ${MANDATEDUSER}" || echo "  [ERROR]  : ${USER} can't submit for ls_priv_user2, mandating ${MANDATEDUSER}"
	${XWBINDIR}/xwsubmit --xwconfig ${MANDATEDUSER}.conf -DMANDATINGUSER=${USER} ls_priv_stickybit_user1  --xwlabel ${USER}_ls_priv_stickybit_user1 > /dev/null && echo "  [SUCCESS]  : ${USER} can submit for ls_priv_stickybit_user1, mandating ${MANDATEDUSER}" || echo "  [ERROR]  : ${USER} can't submit for ls_priv_stickybit_user1, mandating ${MANDATEDUSER}"
done

NBWORKS=$(${XWBINDIR}/xwworks | grep PENDING | wc -l)
echo ""
echo "Public worker workRequests : ${NBWORKS}"
echo ""
for (( i=0 ; i < ${NBWORKS} ; i++ )) ; do
	(( $i % 10 == 0 )) && printf $i
	printf "."
	${XWBINDIR}/xwworkrequest --xwconfig worker.conf > /dev/null
done
echo "Done"

(${XWBINDIR}/xwworks | grep    pub | grep -v RUNNING) && echo "[ERROR] there are non RUNNING public works" || echo "[SUCCESS] all public works are RUNNING"  
(${XWBINDIR}/xwworks | grep -v pub | grep -v PENDING) && echo "[ERROR] there are non PENDING public works" || echo "[SUCCESS] all non public works are PENDING"

NBWORKS=$(${XWBINDIR}/xwworks | grep PENDING | wc -l)
echo ""
printf "Public Vworker workRequests : ${NBWORKS}"
echo ""
for (( i=0 ; i < ${NBWORKS} ; i++ )) ; do
	(( $i % 10 == 0 )) && printf $i
	printf "."
	${XWBINDIR}/xwworkrequest --xwconfig vworker.conf > /dev/null
done
echo "Done"

(${XWBINDIR}/xwworks | grep stickybit | grep -v RUNNING) && echo "[ERROR] there are     stickybit'd works are not RUNNING" || echo "[SUCCESS] all     stickybit'd works are RUNNING"  
(${XWBINDIR}/xwworks | grep stickybit | grep -v PENDING) && echo "[SUCCESS] all non stickybit'd works are PENDING" || echo "[ERROR] there are non stickybit'd works are non PENDING"

(${XWBINDIR}/xwworks | grep priv | grep -v stickybit | grep RUNNING) && echo "[ERROR] there are RUNNING private works" || echo "[SUCCESS] no RUNNING private work"
