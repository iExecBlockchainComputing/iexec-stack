#!/bin/sh

# ------------------- Local Function

Help()
{ 
	echo
	echo `basename $0` "[-h|--help] [-v|--verbose] -f <input file>"
	echo "	-h|--help  : get this help msg";
	echo "	-v|--verbose  "
	echo "	-n|--no-extract : stand-alone jar is recreated with previous extraction "
	echo "	-f <input file>"
	echo
}


#
# we have to cd download because the php files are executed from ../download
#

fileIn=""

verbose=0

while [ $# -gt 0 ]; do
  if [ "$1" = "-v" -o "$1" = "--verbose" ]; then
    verbose=1
  elif [ "$1" = "-f" ]; then
    shift
    fileIn=$1
  elif [ "$1" = "-d" ]; then
    shift
    rep=$1
  fi

  shift
done

if [ "$fileIn" = "" ]; then
  echo "No input file provided :("
  Help
  exit 1
fi

cd download/$rep

if [ $verbose -eq 1 ]; then
  echo Compressing $fileIn
fi

# test what tool to use...
which zip > /dev/null 2>&1
if [ $? -eq 0 ]; then
  ZIPPER="zip"
  ZIPPEROPTS="-r"
else
  which jar > /dev/null 2>&1
  if [ $? -eq 1 ]; then
    ZIPPER="jar"
    ZIPPEROPTS="uf"
  else
    echo "No compressor tool found :("
    exit 1
  fi
fi

if [ "$fileIn" = "XWClient.jar" ]; then
  $ZIPPER $ZIPPEROPTS $fileIn data
else
  $ZIPPER $ZIPPEROPTS $fileIn xwclasses data
fi

retVal=$?

if [ $verbose -eq 1 ]; then
  if [ $retVal -eq 0 ]; then
    echo $fileIn compressed
  else
    echo $fileIn compression error
  fi
fi

exit $retVal
