#
# Copyrights     : CNRS
# Author         : Oleg Lodygensky
# Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
# Web            : http://www.xtremweb-hep.org
# 
#      This file is part of XtremWeb-HEP.
#
#    XtremWeb-HEP is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    XtremWeb-HEP is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
#
#



NBLIGNE=232 #wc -l header.sh + 4

die() {
	echo "$*" >&2
	echo "Aborting..." >&2
	exit 1
}

confirm_or_die() {
	while [ 1 -eq 1 ]; do
		printf "[yes/no] >>> "
		read CONFIRM
		case $CONFIRM in
			[yY]*) return;;
			[nN]*) die "OK.";;
		esac
	done
}

cat << EOF

    XtremWeb Worker Installation

    The files composing the Xtrem Web Worker Software may be installed
    in your home directory: ${HOME}/.xtremweb
    
EOF

#decompressing the distrib
    echo
    echo " Self Extraction"
    echo

tail +$NBLIGNE $0 | tar zxv
cd XtremWeb.worker/

#Checking that all the files are there
for file in  jcert.jar jnet.jar jsse.jar XWW.jar.${VERSION} MinML.jar README.worker xwcert.keys xwrc.sample; do 
    if ! test -e $file; then
	echo " The files"  $file " is missing, you should download"
	echo " the whole distribution at http://xtremweb.net"
	die
    fi
done

#Making required directories
    echo
    echo " Making directory"
    echo

for rep in .xtremweb .xtremweb/bin .xtremweb/classes .xtremweb/classes/lib .xtremweb/lib .xtremweb/lib/security .xtremweb/binCache; do
    [ -d $rep ] || install -d $HOME/$rep
done

    echo
    echo " Installing the configuration file"
    echo

    if test -e $HOME/.xtremweb/xwrc; then
	echo " It appears that you already have a file configuration "
	echo " file installed in your Home directory."
	echo
	echo " Should we overwrite it and make a backup ?"
#	confirm_or_die

	while [ 1 -eq 1 ]; do
		printf "yes/no >>> "
		read CONFIRM
		case $CONFIRM in
			[yY]*)
			    echo " good, your file will be backed up."
			    break
			;;
			[nN]*) 
			    install_conf_file="no"
			    break
			;;
		esac
	done
    fi

    if test -z $install_conf_file; then

	sed  s/LOG/$XWLOGIN/ ./xwrc.sample > xwrc.tmp 
	sed  s/PASS/$XWPASS/ ./xwrc.tmp > xwrc
	
	install -b  --mode=644 ./xwrc $HOME/.xtremweb/xwrc
	rm -f ./xwrc ./xwrc.tmp
	
    fi

    echo

    install  --mode=644 ./XWW.jar.${VERSION} $HOME/.xtremweb/classes/XWWLaunch.jar
    install  --mode=644 ./XWW.jar.${VERSION} $HOME/.xtremweb/classes/lib/XWW.jar.${VERSION}
    install  --mode=644 ./jnet.jar $HOME/.xtremweb/classes/lib/jnet.jar
    install  --mode=644 ./jcert.jar $HOME/.xtremweb/classes/lib/jcert.jar
    install  --mode=644 ./jsse.jar $HOME/.xtremweb/classes/lib/jsse.jar 
    install  --mode=644 ./log4j.jar $HOME/.xtremweb/classes/lib/log4j.jar 
    
#Searching Java
    java=`java >& /dev/null`
    if [ $? -lt 127 ]; then
	java=`which java`
    else
    # testing $JAVA_HOME is set
	if [ -n "$JAVA_HOME" -a  -x "$JAVA_HOME/bin/java" ]; then 
	    java="$JAVA_HOME/bin/java"
	    echo " \$JAVA_HOME is set to " $JAVA_HOME
	else
	# ask user
	    echo " I cannot find Java, if you don't have java, download it!"
	    echo " please, enter the correct path to the java executable:"
	    echo " e.g.  [PATH_TO_JDK]/bin/java"
	    while [ -n "$java" -a -x "$java" -a ! -d "$java" ]; do
		printf ">>> "
		read java
	   done
	fi
    fi

    echo " Full path to java is: " $java

    # What version
    java_version=$($java -version 2>&1 | grep "java version" |awk  '{ print $3;}' |tr \" " ")
    echo "Java Version is " $java_version 
    echo
    case $java_version in
# jdk >1.2
	?1.[23]*)
	    [ -e ./xtremweb ] && rm -f xtremweb
cat > ./xtremweb <<EOF
#!/bin/sh
if test -z \$CLASSPATH; then
    export CLASSPATH=${HOME}/.xtremweb/classes/lib/log4j.jar:${HOME}/.xtremweb/classes/lib/jsse.jar:${HOME}/.xtremweb/classes/lib/jcert.jar:${HOME}/.xtremweb/classes/lib/jnet.jar:${HOME}/.xtremweb/lib/XWW.jar.${VERSION}
else
    export CLASSPATH=${HOME}/.xtremweb/classes/lib/log4j.jar:${HOME}/.xtremweb/classes/lib/jsse.jar:${HOME}/.xtremweb/classes/lib/jcert.jar:${HOME}/.xtremweb/classes/lib/jnet.jar:${HOME}/.xtremweb/lib/XWW.jar.${VERSION}:\$CLASSPATH

fi
if test -z \$LD_LIBRARY_PATH; then
    export LD_LIBRARY_PATH=${HOME}/.xtremweb/lib/
else
    export LD_LIBRARY_PATH=${HOME}/.xtremweb/lib/:\$LD_LIBRARY_PATH
fi
${java} xtremweb.upgrade.Launcher -f ../xwrc
EOF
		;;
	?1.1*)
# jdk <1.2 not supported any more ???
	    [ -e ./xtremweb ] && rm -f xtremweb
	    echo "Versions of Java older than 1.2 are no longer supported."
	    echo "Please upgrade to jdk1.2 at least"
	    die
cat > ./xtremweb <<EOF
#!/bin/sh
if test -z \$LD_LIBRARY_PATH; then
    export LD_LIBRARY_PATH=${HOME}/.xtremweb/lib/
else
    export LD_LIBRARY_PATH=${HOME}/.xtremweb/lib/:\$LD_LIBRARY_PATH
fi
if test -z \$CLASSPATH; then
    export CLASSPATH=${HOME}/.xtremweb/lib/XWW.jar
else
    export CLASSPATH=${HOME}/.xtremweb/lib/XWW.jar:\$CLASSPATH
fi
${java} XWS
EOF
		;;
	*)
	    echo "I cannot determine the version of java"
	    die
	    ;;
    esac
	 
    [ -e ./xtremweb ] && install  --mode=744 ./xtremweb $HOME/.xtremweb/bin/xtremweb

#Installing the XtremWeb Certificate
    echo
    echo " Installing the XtremWeb certificate (authentication to the server)"
    echo
# removing the previous one
    [ -e $HOME/.xtremweb/lib/security/keystore.worker ] && rm -f $HOME/.xtremweb/lib/security/keystore.worker
    keytool=`keytool   2>&1 /dev/null`
    if [ $? -lt 127 ]; then
# It looks like
    echo " XtremWeb Certificate "
    echo
keytool -printcert -file xwcert.keys
    echo
    echo " Generating your private/public keys "
    echo
#generate private/public keys for worker, during installation
keytool -genkey -dname "cn=worker,  o=XtremWeb" -alias xtremweb.worker -storepass XWCertPass -keypass XWCertPass -keystore $HOME/.xtremweb/lib/security/keystore.worker -validity 365 -v

# Now import the certificate in the list of trusted certificate
keytool -import -alias xtremweb.server -file xwcert.keys -trustcacerts -keystore $HOME/.xtremweb/lib/security/keystore.worker -storepass XWCertPass -noprompt -v
    else
	echo "You must have the keytool utility in your path"
	echo "Keytool allows you to import the XtremWeb certificate"
	die
    fi
#some cleaning
    cd ..
    rm -rf XtremWeb.worker
    echo
    echo " Xtrem Web Worker can be launched  with "
    echo " $HOME/.xtremweb/bin/xtremweb" 
    echo

    echo " Launch XtremWeb Worker now ?"
    
    while [ 1 -eq 1 ]; do
		printf "[yes]/no >>> "
		read CONFIRM
		[ -z $CONFIRM ] && CONFIRM="yes"
		case $CONFIRM in
			[yY]*)
			    echo " Starting XtremWeb"
			    $HOME/.xtremweb/bin/xtremweb &
			    break
			;;
			[nN]*) 
			    echo 
			    break
			;;
		esac
	done
    
exit 1

