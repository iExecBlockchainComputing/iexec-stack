#!/bin/sh

############################################################
# Let first reach "real" file through symbolic links, if any
############################################################

while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '^.*-> \(.*\)$' 2>/dev/null`
    if expr "$link" : '^/' 2> /dev/null >/dev/null; then
        PRG="$link"
    else
        PRG="`dirname $PRG`/$link"
    fi
done

############################################################
fatal (){
	echo $1
	exit 1
} 


############################################################


progdir=`dirname $0`
progname=`basename $0`

[ $# -lt 2 ] && fatal "Usage (lower) : $progname <keystorefile> <keystorepassword>"

type keytool > /dev/null 2>&1 || fatal "Keytool is not installed; please install it and/or correct your \$PATH env variable"


currentDir=`pwd`
cd $progdir/..
ROOTDIR=`pwd`
cd $currentDir

CACERTSFILENAME="cacerts"
SYSCACERTSFILE=$JAVA_HOME/jre/lib/security/${CACERTSFILENAME}
[ ! -r ${SYSCACERTSFILE} ] && SYSCACERTSFILE=/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/${CACERTSFILENAME}
[ ! -r ${SYSCACERTSFILE} ] && fatal "Can't find cacerts"

CACERTSFILE=${CACERTSFILENAME}

cp -f ${SYSCACERTSFILE} ${CACERTSFILE} || fatal "Can't copy cacerts" 


KEYSTOREFILENAME=$1
KEYSTOREFILE=${KEYSTOREFILENAME}
KEYSTOREPASS=$2

TRUSTEDCERTSFILENAME="trustedcerts.txt"
TRUSTEDCERTSFILE=${TRUSTEDCERTSFILENAME}
KEYTOOL=keytool

if [ ! -r ${CACERTSFILE} ] ; then
	echo "File not found ${CACERTSFILE}"
	exit 1
fi


${KEYTOOL} -list -keystore ${CACERTSFILE} > ${TRUSTEDCERTSFILE} << LISTEOF

LISTEOF

for trustedcert in `cat ${TRUSTEDCERTSFILE} | grep trusted | cut -d ',' -f 1 ` ; do

	echo "trustedcert = $trustedcert"
	TRUSTEDCERTFILE="${trustedcert}.pem"

	${KEYTOOL} -export  -keystore ${CACERTSFILE} -alias ${trustedcert} -file ${TRUSTEDCERTFILE} << EXPORTEOF

EXPORTEOF

	[ $? -eq 0 ] || echo "Can't export ${trustedcert}"

	KEYTOOLCMD="${KEYTOOL} -import -alias ${trustedcert} -file ${TRUSTEDCERTFILE} -trustcacerts -keystore ${KEYSTOREFILE} -storepass ${KEYSTOREPASS}"

	echo $LANG | grep "fr" > /dev/null
	if [ $? -eq 0 ] ; then
		${KEYTOOLCMD} << IMPORTEOF_FR
o
IMPORTEOF_FR

	else
		${KEYTOOLCMD} << IMPORTEOF_EN
o
IMPORTEOF_EN
	fi

	[ $? -eq 0 ] || echo "Can't import ${trustedcert}"

	rm -f  ${TRUSTEDCERTFILE}

done


${KEYTOOL} -list -keystore ${KEYSTOREFILE} -storepass ${KEYSTOREPASS}
