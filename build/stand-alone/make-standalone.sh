#!/bin/sh
#
# File    : make-standalone.sh
# Author  : Oleg Lodygensky (lodygens /at\ lal.in2p3.fr)
# Date    : Feb 4th, 2005
# Purpose : this scripts helps to generate an XWHEP worker JAR file 
#           for all platforms in including all known jni parts in one jar file.
#           You must provide a directory name where his script will find
#           the different jni parts.
# Params  : -h to get some help
#           -i to provide the directory where to find the jni parts
#           (default is ../platforms/worker/)
#           -d set debug
#
# This script is supposed to be in the build/ directory
#
# By default, this script looks for a ./platforms/worker/ directory as follow:
#  ../platforms/worker/linux-amd64/jni   : linux amd64     jni libraries
#  ../platforms/worker/linux-ix86/jni    : linux ix86      jni libraries
#  ../platforms/worker/macosx-ppc/jni    : mac OS X ppc    jni libraries
#  ../platforms/worker/macosx-ix86/jni   : mac OS X ix86   jni libraries
#  ../platforms/worker/macosx-x86_64/jni : mac OS X x86_64 jni libraries
#  ../platforms/worker/win32-ix86/jni    : win32 ix86      jni libraries
#  ../ etc etc 
#
# If you provide your own directory, it must follow the same structure.
#
# Each of these directories should have :
#   - XWUtil.jni
#   - XwTracer.jni
#   - XWInterrupts.jni
#   - XWNotify.jni
#   - PortMapper.jni
#
# Prerequisists : sucessfull make
#
# Results : this scripts generates
#           * a new JAR file 
#

# ------------------- Local Function

Help()
{ 
    echo
    echo `basename $0` "[-h|--help] [-v|--verbose] [-n|--no-extract] -f <input file>"
    echo "	-h|--help  : get this help msg";
    echo "	-v|--verbose  "
    echo "	-n|--no-extract : stand-alone jar is recreated with previous extraction "
    echo "	-f <input file>"
    echo
    if [ "$1" != "" ]; then
        echo "========================="
        echo "ERROR : $1"
        echo "========================="
    fi

    exit 1
}


ROOT=`dirname $0`
[ $ROOT = "." ] && ROOT=`pwd`
BASEDIR=$ROOT/../..
BUILDDIR=$BASEDIR/build
INDIR=$BUILDDIR/platforms/worker
VFILE=$BUILDDIR/VERSION

fileIn=""
OUTJAR=""

extract=1
verbose=0

while [ $# -gt 0 ]; do
    if [ "$1" = "-v" -o "$1" = "--verbose" ]; then
        verbose=1
    elif [ "$1" = "-n" -o "$1" = "--no-extract" ]; then
        extract=0
    elif [ "$1" = "-f" ]; then
        shift
        fileIn=$1
    elif [ "$1" = "-h" -o "$1" = "--help" ]; then
        Help
    fi

    shift
done

[ "$fileIn" = "" ] && Help "No input file provided :("
[ ! -f $fileIn ] && Help "$fileIn not found"


#
# extracting 3rd party classes
#
jar xf $BASEDIR/classes/JOpenId-1.08.jar
jar xf $BASEDIR/classes/MinML.jar
jar xf $BASEDIR/classes/antlr.jar
jar xf $BASEDIR/classes/commons-codec-1.3.jar
jar xf $BASEDIR/classes/commons-httpclient-3.0.1.jar
jar xf $BASEDIR/classes/commons-logging-1.1.jar
jar xf $BASEDIR/classes/servlet-api-3.0.jar
jar xf $BASEDIR/classes/log4j-1.2.17.jar
#jar xf $BASEDIR/classes/attic-all-0.3.jar

if [ $extract -eq 0 ]; then

    [ $verbose -eq 1 ] && echo "not extracting $fileIn"

else

#
# extracting XWHEP package
#
#  rm -rf xwhep/ xwclasses/ jni/
    [ $verbose -eq 1 ] && "echo Extracting $fileIn"
    
    jar xf $fileIn
    
    [ $verbose -eq 1 ] && echo "$fileIn extracted"
fi


#
# copying available native codes
#
PLATFORMS="linux-ix86 linux-amd64 macosx-ppc macosx-ix86 macosx-x86_64 win32-ix86 win32-amd64"
JNIS="PortMapper.jni XWUtil.jni XwTracer.jni XWInterrupts.jni XWNotify.jni"

OS=`uname -s`
echo "OS=$OS"
ARCH="${XWARCH}"
echo "ARCH=$ARCH"
echo "TARGE=$TARGET"

if [ "$TARGET" == "Mac OS X" ]; then 
    OS="macosx"
fi

#if [ "$OS" == "Linux" ]; then 
#    OS="linux"
#    CPU=`uname -m`
#    [ "$CPU" == "x86_64" ] && ARCH="amd64"
#    
#elif [ "$OS" == "Darwin" ]; then
#    OS="macosx"
#    ARCH="ppc"
##    CPU=`uname -m`
#    `rm -f toto`
#    `sysctl hw.optional | awk -F': ' '/64/ {print $2}' 2>&1 > toto`
#    CPU=`cat toto`
#    echo "00 $CPU"
#    [ "$CPU" == "0" ] && ARCH="ix86"
#    [ "$CPU" == "1" ] && ARCH="x86_64"
#    echo "01 $ARCH"
#    `rm -f toto`
#elif [ "$OS" == "CYGWIN_NT-5.1" ]; then 
#    OS=win32
#fi
#

if [ ! -f $VFILE ]; then
    echo "$VFILE not found (?)"
    exit 1
fi

#
# uppercases to comply to 
#     - src/common/XWCPUs.java
#     - src/common/XWOSes.java
#
OS=`echo $OS | tr "[:lower:]" "[:upper:]"`
ARCH=`echo $ARCH | tr "[:lower:]" "[:upper:]"`


#
# Retreive version
#
V_BRANCH=`cat $VFILE | tail -1 | head -1 | cut -d '=' -f 2`
V_MICRO=`cat $VFILE | tail -2 | head -1 | cut -d '=' -f 2`
V_MINOR=`cat $VFILE | tail -3 | head -1 | cut -d '=' -f 2`
V_MAJOR=`cat $VFILE | tail -4 | head -1 | cut -d '=' -f 2`

VERSION=$V_MAJOR.$V_MINOR.$V_MICRO-$V_BRANCH

[ $verbose -eq 1 ] && echo "Compressing XWHEP $VERSION"


#
# Extracting xwhep.jar file
#
VDIR=$BUILDDIR/$VERSION
INJARNAME="xtremweb-$OS-$VERSION.jar"
INJAR=$ROOT/$INJARNAME

OUTJAR="xtremweb-allplatforms-$VERSION.jar"

rm -Rf $INJAR $OUTJAR $VERSION
[ $verbose -eq 1 ] && echo "cp $fileIn $INJAR"
cp $fileIn $INJAR

[ -d $VDIR ] || mkdir $VDIR

[ $verbose -eq 1 ] && echo Expending $INJARNAME

jar xvf ../$INJARNAME > /dev/null 2>&1

rm -f jni/*


# Keep the last compiled version

JNIDIR=`echo "$OS-$ARCH" | tr "[:upper:]" "[:lower:]"`
JNIDIR="$JNIDIR/jni"

cp -f $BUILDDIR/classes/jni/XWInterrupts.* $INDIR/$JNIDIR/XWInterrupts.jni
cp -f $BUILDDIR/classes/jni/XWNotify.* $INDIR/$JNIDIR/XWNotify.jni
cp -f $BUILDDIR/classes/jni/XWUtil.* $INDIR/$JNIDIR/XWUtil.jni
cp -f $BUILDDIR/classes/jni/XwTracer.* $INDIR/$JNIDIR/XwTracer.jni


# copy everything

for jni in $JNIS; do
    for platform in $PLATFORMS; do
        inFile="$INDIR/$platform/jni/$jni"
        PLATFORM=`echo $platform | tr "[:lower:]" "[:upper:]"`
        outFile="./jni/$jni.$VERSION.$PLATFORM"
        if [ -f $inFile ]; then
            cp $inFile $outFile > /dev/null 2>&1
            [ $verbose -eq 1 ] && echo "$inFile copied to $outFile"
		else
            [ $verbose -eq 1 ] && echo "$inFile not found"
        fi
    done
done



#
# creating a single JAR file
#
rm -f $OUTJAR
[ $verbose -eq 1 ] && echo Compressing $OUTJAR


[ $verbose -eq 1 ] && echo "jar cvfm $OUTJAR META-INF/MANIFEST.MF *"
jar cvfm $OUTJAR META-INF/MANIFEST.MF CHANGELOG COM LICENSE antlr com javax jni org uk xtremweb data > /dev/null 2>&1

if [ $verbose -eq 1 ]; then
    echo $OUTJAR compressed
fi

if [ $verbose -eq 1 ]; then 
    echo ""
    echo "*====================================================*"
    echo "* Now in $OUTJAR"
    echo "*====================================================*"
    jar tvf $OUTJAR | grep jni
fi

rm -f xtremweb.jar
ln -s $OUTJAR xtremweb.jar

