#!/bin/sh
#=============================================================================
#
#  Purpose  :  Shell script inserting a Docker container as an XtremWeb-HEP application
#
#  OS       :  Linux, Mac OS X
#
#  Syntax   :  xwadddockerapp.sh  [-v | --verbose]
#                             [--xwconfig <Client configuration file for
#                                          XtremWeb-HEP>]
#                             [--startsh  <bash script to start the container>]
#                             [--stopsh   <bash script to stop  the container>]
#                             [--startcmd <cmd  script to start the container>]
#                             [--stopcmd  <cmd  script to stop  the container>]
#                             [--name <XtremWeb-HEP app name>]
#
# Changelog:
#             $Log: xwaddvbapp.sh,v $
#  Jul  3rd, 2017 : creation by Oleg Lodygensky
#                        based on xwaddvbapp.sh, by O. Lodygensky and E. Urbah
#
#=============================================================================


#=============================================================================
#
#  Copyright (C) 2011-2012 Oleg LODYGENSKY
#                          at LAL, Univ Paris-Sud, IN2P3/CNRS, Orsay, France
#
#  Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria
#  Web            : http://www.xtremweb-hep.org
# 
#    This file is part of XtremWeb-HEP.
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
#=============================================================================


#=============================================================================
#
#  GLOBAL VARIABLES
#
#=============================================================================
if [ -z "${OSTYPE##linux*}" ]; then
  DATE_FORMAT='--rfc-3339=seconds'
else
  DATE_FORMAT='+%Y-%m-%d %H:%M:%S%z'
fi

XW_APP_TYPE='DOCKER'

#-----------------------------------------------------------------------------
#  GLOBAL VARIABLES containing parameters provided to this script
#-----------------------------------------------------------------------------
XW_APP_NAME=''
CFG=''
VERBOSE=''
DRY_RUN=''

#-----------------------------------------------------------------------------
#  GLOBAL VARIABLES containing URIs to be deleted in case of failure
#-----------------------------------------------------------------------------
START_SH_URI=''
STOP_SH_URI=''
START_BAT_URI=''
STOP_BAT_URI=''

#-----------------------------------------------------------------------------
#  GLOBAL VARIABLES set as return values by functions
#-----------------------------------------------------------------------------
XW_MESSAGE=''                 #  Set by function  xw_action_success_or_clean
XW_ATTRIBUTE_VALUE=''         #  Set by function  extract_xw_attribute_value
DATA_NAME=''                  #  Set by function  verify_data_get_name_and_uri
DATA_URI=''                   #  Set by functions verify_data_get_name_and_uri
                              #               and xw_send_file

#-----------------------------------------------------------------------------
#  XtremWeb-HEP commands are searched first in the folder containing this
#  script, and then in the standard install location
#-----------------------------------------------------------------------------
SCRIPT_FOLDER=$(dirname "$0")
XW_PROG="xtremweb.client"

XW_BIN="$SCRIPT_FOLDER"
if [ ! -f "$XW_BIN/$XW_PROG" ]; then
  XW_BIN=$(ls -d /opt/xwhep-client-*  |  tail -1)/bin
  [ -f "$XW_BIN/$XW_PROG" ]  ||  fatal  "'$XW_PROG' NOT found"
fi

XW_RM="$XW_BIN/xwrm"
XW_APPS="$XW_BIN/xwapps"
XW_DATAS="$XW_BIN/xwdatas"
XW_USERS="$XW_BIN/xwusers"
XW_VERSION="$XW_BIN/xwversion"
XW_SEND_APP="$XW_BIN/xwsendapp"
XW_SEND_DATA="$XW_BIN/xwsenddata"


#=============================================================================
#  Function  debug_message (Message part, ...)
#=============================================================================
debug_message ()
{
  echo "$(date "$DATE_FORMAT")  $0  DEBUG: " "$@"  > /dev/stderr
}


#=============================================================================
#  Function  info_message (Message part, ...)
#=============================================================================
info_message ()
{
  echo "$(date "$DATE_FORMAT")  $0  INFO: " "$@"
}


#=============================================================================
#  Function  fatal (Message part, ...)
#=============================================================================
fatal ()
{
  RC=$?
  if [ $RC -eq 0 ]; then RC=1; fi
  
  echo "$(date "$DATE_FORMAT")  $0  FATAL:  ${*:-Ctrl+C}"  > /dev/stderr
  exit $RC
}



#=============================================================================
#
#  Function  fatal_message_clean_exit  (Message part, ...)
#
#  -  Display a fatal message,
#  -  Delete URIs defined in non-empty global variables,
#  -  Exit
#
#=============================================================================
fatal_message_clean_exit()
{
  RC=$?
  if [ $RC -eq 0 ]; then RC=1; fi
  
  echo "$(date "$DATE_FORMAT")  $0  FATAL: "  "$@"
  
  if [ "$DEFAULT_DATA_DELETE_URI$LIVE_CD_DELETE_URI$START_SH_URI" ]; then
    ( [ -z "$VERBOSE" ]  ||  { echo > /dev/stderr; set -x; }
      "$XW_RM"  $CFG  $DEFAULT_DATA_DELETE_URI  $LIVE_CD_DELETE_URI    \
                      $START_SH_URI                $STOP_SH_URI  \
                      $START_BAT_URI               $STOP_BAT_URI )
  fi
  
  exit $RC
}


#=============================================================================
#
#  Function  xw_action_success_or_clean ()
#
#  Parameters :    XtremWeb-HEP action
#                  XtremWeb-HEP params ...
#
#  Variable set :  XW_MESSAGE :   Output of XtremWeb-HEP
#
#=============================================================================
xw_action_success_or_clean ()
{
  XW_MESSAGE=$( [ -z "$VERBOSE" ]  ||  set -x;  "$@" )  ||  \
    fatal_message_clean_exit  "$XW_MESSAGE"
}


#=============================================================================
#
#  Function  extract_xw_attribute_value ()
#
#  From the XML output of an XtremWeb-HEP command, extract an attribute value
#
#  Parameters :
#      XW_DESCRIPTION :      Description of the XtremWeb-HEP object
#      XW_ATTRIBUTE_NAME :   Name of the XtremWeb-HEP attribute to extract
#      XW_XML :              XML output of an XtremWeb-HEP command
#
#  Variable set :
#      XW_ATTRIBUTE_VALUE :  Extracted value of the XtremWeb-HEP attribute
#
#=============================================================================
extract_xw_attribute_value ()
{
  XW_DESCRIPTION="$1"
  XW_ATTRIBUTE_NAME="$2"
  XW_XML="$3"

  XW_ATTRIBUTE_VALUE=$(perl -we  \
    'if ( $ARGV[0] =~ /(<'"$XW_ATTRIBUTE_NAME"'>)(.*)(<\/'"$XW_ATTRIBUTE_NAME"'>)/i )
        {print $2}'  "$XW_XML")
  [ "$XW_ATTRIBUTE_VALUE" ]  ||  \
    fatal  "Can NOT extract '$XW_ATTRIBUTE_NAME' of $XW_DESCRIPTION from"  \
           "'$XW_XML'"
}


#=============================================================================
#
#  Function  xw_send_file ()
#
#  Insert the local file given in parameter inside XtremWeb-HEP
#
#  Parameter :     FILE_PATH :  Local file path
#
#  Variable set :  DATA_URI :   Data URI
#
#=============================================================================
xw_send_file ()
{
  if [ -z "$1" ] ; then
    DATA_URI=""
    return
  fi
  FILE_PATH="$1"
  FILE_NAME="${FILE_PATH##*/}"
  echo "sendfile senddata = $XW_SEND_DATA cfg = $CFG  filename = $FILE_NAME filepath = $FILE_PATH"
  xw_action_success_or_clean  "$XW_SEND_DATA"  $CFG  "$FILE_NAME"  \
                                                     "$FILE_PATH"
  DATA_URI=$(echo "$XW_MESSAGE"  |  grep '^xw://')
  [ "$DATA_URI" ]  ||  fatal_message_clean_exit  "$XW_MESSAGE"
}


#=============================================================================
#
#  Function  verify_data_get_name_and_uri ()
#
#  Verify the reference of a data, get its name and try to get its URI.
#
#  Parameters :
#      DATA_DESCRIPTION :       Data description
#      DATA_REF :               Local file path or http(s):// or xw:// or UUID
#      ACCEPTABLE_EXTENSIONS :  Optional list of acceptable extensions
#
#  Variables set :
#      DATA_URI :               Data URI
#      DATA_NAME :              Real data name
#
#=============================================================================
verify_data_get_name_and_uri ()
{
  DATA_DESCRIPTION="$1"
  DATA_REF="$2"
  ACCEPTABLE_EXTENSIONS="$3"
 
  DATA_NAME=''
  DATA_URI=''
  
  D1='[0-9A-Fa-f]'
  D4="$D1$D1$D1$D1"
  UUID_PATTERN="$D4$D4-$D4-$D4-$D4-$D4$D4$D4"

  #---------------------------------------------------------------------------
  #  If the data reference begins with 'http://' or 'https://', extract its
  #  name
  #---------------------------------------------------------------------------
  if [ '(' -z "${DATA_REF##http://*}"  ')' -o  \
       '(' -z "${DATA_REF##https://*}" ')' ]; then
    
    DATA_NAME="${DATA_REF%%[/?]download}"
    DATA_NAME="${DATA_NAME##*/}"
    DATA_NAME="${DATA_NAME##*&filename=}"
    
  #---------------------------------------------------------------------------
  #  If the data reference begins with 'xw://' or looks like an UUID, verify
  #  in the XtremWeb-HEP server that it exists, has a name and has an uid
  #---------------------------------------------------------------------------
  elif [ -z "${DATA_REF##xw://*}" ]  ||  \
       expr "$DATA_REF" : "$UUID_PATTERN\$" > /dev/null; then
    
    xw_action_success_or_clean  "$XW_DATAS"  $CFG  "$DATA_REF"  --xwformat xml
    
    OBJECT_TYPE=$( perl -we 'if ( $ARGV[0] =~ m/<data>.*<\/data>/i )
                             {print "data"}'  "$XW_MESSAGE" )  ||  \
      fatal  "Can NOT retrieve type of '$DATA_REF' from '$XW_MESSAGE'"
    
    [ "$OBJECT_TYPE" = "data" ]  ||  \
      fatal  "XtremWeb-HEP object '$DATA_REF' is of type '$OBJECT_TYPE'"  \
             "instead of 'data'"
    
    extract_xw_attribute_value  "data '$DATA_REF'"  'name'  "$XW_MESSAGE"
    DATA_NAME="$XW_ATTRIBUTE_VALUE"
    
    extract_xw_attribute_value  "data '$DATA_REF'"  'uri'   "$XW_MESSAGE"
    DATA_URI="$XW_ATTRIBUTE_VALUE"

  #---------------------------------------------------------------------------
  #  Otherwise, the data reference is the name of a local file.
  #  It has NO uri yet.
  #---------------------------------------------------------------------------
  else
    [ -r "$DATA_REF" ]  ||  \
      fatal  "Can NOT read $DATA_DESCRIPTION '$DATA_REF'"
    DATA_NAME="${DATA_REF##*/}"
  fi
  
  #---------------------------------------------------------------------------
  #  If acceptable extensions are given, verify that DATA_NAME ends with one
  #---------------------------------------------------------------------------
  if [ "$ACCEPTABLE_EXTENSIONS" ]; then
    DATA_EXTENSION="${DATA_NAME%.[Zz][Ii][Pp]}"
    DATA_EXTENSION="${DATA_EXTENSION##*.}"
    if [ -z "$DATA_EXTENSION" ]  ||  \
       ! expr  "$ACCEPTABLE_EXTENSIONS" : ".*'$DATA_EXTENSION'"  > /dev/null
    then
      fatal  "$DATA_DESCRIPTION '$DATA_NAME' does NOT end with one of"  \
             "$ACCEPTABLE_EXTENSIONS"
    fi
  fi

  #---------------------------------------------------------------------------
  #  If the data reference begins with 'http://' or 'https://', verify that
  #  it can be downloaded
  #---------------------------------------------------------------------------
  if [ '(' -z "${DATA_REF##http://*}"  ')' -o  \
       '(' -z "${DATA_REF##https://*}" ')' ]; then
    
    ( [ -z "$VERBOSE" ]  ||  set -x
      wget  -nv  --timeout=5  --tries=1  -O /dev/null  "$DATA_REF" )  ||  \
      fatal  "Can NOT retrieve $DATA_DESCRIPTION '$DATA_REF'"
    
    DATA_URI="$DATA_REF"
  fi
}



#=============================================================================
#
#  MAIN
#
#=============================================================================

#-----------------------------------------------------------------------------
#  Process parameters
#-----------------------------------------------------------------------------
while [ $# -gt 0 ]
do

  case "$1" in
    
    --name )
      shift
      XW_APP_NAME="$1"
      ;;
    
    --xwconfig )
      shift
      CFG="--xwconfig $1"
      unset X509_USER_PROXY
      ;;
    
    --startsh )
      shift
      XW_START_CONTAINER_SH="$1"
      ;;
    
    --stopsh )
      shift
      XW_STOP_CONTAINER_SH="$1"
      ;;

    --startcmd )
      shift
      XW_START_DOCKER_BAT="$1"
      ;;
    
    --stopcmd )
      shift
      XW_STOP_DOCKER_BAT="$1"
      ;;

    --dockerfile )
      shift
      DEFAULT_DATA_REF="$1"
      ;;

    -v | --verbose | --debug )
      VERBOSE=1
      ;;
    
    --dry | --dry*run )
      DRY_RUN=1
      VERBOSE=1
      ;;
    
    -h | --help | '-?' )
      cat << END_OF_USAGE
      Usage:  $0  [-v | --verbose]
           [--xwconfig   <Client configuration file for XtremWeb-HEP>]
           [--startsh    <bash script to start the container>]
           [--stopsh     <bash script to stop  the container>]
           [--startcmd   <cmd  script to start the container>]
           [--stopcmd    <cmd  script to stop  the container>]
           [--dockerfile <Dockerfile>]
           [--name <XtremWeb-HEP app name>]
END_OF_USAGE
      exit 0
      ;;
    
  esac
  
  shift
  
done


#-----------------------------------------------------------------------------
#  Verify that mandatory parameters are really provided
#-----------------------------------------------------------------------------
[ "$XW_APP_NAME" ]  ||  fatal  "You must provide an application name"  \
                               "(please try --help)"

[ "$XW_START_CONTAINER_SH" -o "$XW_START_DOCKER_BAT" ]  ||  fatal  "You must provide startup scripts "  \
                               "(please try --help)"

if [ -z "$XW_APP_NAME" ]; then
  XW_APP_NAME="$DATA_NAME"
fi
  
DEFAULT_DATA_URI=''
DEFAULT_DATA_SPEC=''
if [ "$DEFAULT_DATA_REF" ]; then
  verify_data_get_name_and_uri  'default data'  "$DEFAULT_DATA_REF"

  #---------------------------------------------------------------------------
  #  Here BOTH references have been verified
  #---------------------------------------------------------------------------
  if [ -z "$DATA_URI"  ]; then
    xw_send_file  "$DEFAULT_DATA_REF"
    DEFAULT_DATA_DELETE_URI="$DATA_URI"
  fi
  DEFAULT_DATA_URI="$DATA_URI"
  DEFAULT_DATA_SPEC="<defaultdirinuri>$DATA_URI</defaultdirinuri>"
fi
if [ "$VERBOSE" ];  then
  debug_message "DEFAULT_DATA_URI='$DEFAULT_DATA_URI'"
  echo  > /dev/stderr
fi




[ -f "$XW_START_CONTAINER_SH" ]   ||  XW_START_CONTAINER_SH=""
[ -f "$XW_STOP_CONTAINER_SH" ]    ||  XW_STOP_CONTAINER_SH=""
[ -f "$XW_START_DOCKER_BAT" ]  ||  XW_START_DOCKER_BAT=""
[ -f "$XW_STOP_DOCKER_BAT" ]   ||  XW_STOP_DOCKER_BAT=""

if [ "$VERBOSE" ]; then
 echo  > /dev/stderr
 debug_message "XW_START_CONTAINER_SH ='$XW_START_CONTAINER_SH'"
 debug_message "XW_STOP_CONTAINER_SH  ='$XW_STOP_CONTAINER_SH'"
 debug_message "XW_START_DOCKER_BAT='$XW_START_DOCKER_BAT'"
 debug_message "XW_STOP_DOCKER_BAT ='$XW_STOP_DOCKER_BAT'"
fi


#-----------------------------------------------------------------------------
#  Get the current XtremWeb-HEP user
#-----------------------------------------------------------------------------
[ -z "$VERBOSE" ]  ||  echo

xw_action_success_or_clean  "$XW_VERSION"  $CFG  --xwverbose

XW_USER=$(echo  "$XW_MESSAGE"  |  grep  '^ *login *='  |  \
                                  sed  -e 's/^ *login *= *//')

[ "$XW_USER" ]  ||  \
  fatal  "Can NOT extract XtremWeb-HEP user name from '$XW_MESSAGE'"

info_message "XW_USER=$XW_USER"

#-----------------------------------------------------------------------------
#  Get the access rights of the current XtremWeb-HEP user
#-----------------------------------------------------------------------------
xw_action_success_or_clean  "$XW_USERS"  $CFG  "$XW_USER"  --xwformat xml

extract_xw_attribute_value  "user '$XW_USER'"  'rights'  "$XW_MESSAGE"
XW_RIGHTS="$XW_ATTRIBUTE_VALUE"

if [ "$XW_RIGHTS" != "SUPER_USER" ]; then
  info_message "        Rights != SUPER_USER"
  echo -n "Application will NOT be public. "  \
          "Do you really want to continue [y/N] "
  read ANSWER
  [ '(' "$ANSWER" = "Y" ')' -o '(' "$ANSWER" = "y" ')' ]  ||  exit 1
fi


if [ -z "$XW_APP_NAME" ]; then
  XW_APP_NAME="$DATA_NAME"
fi
  


#-----------------------------------------------------------------------------
#  Define substitution expressions for PERL to create shell and BAT scripts
#  from templates
#-----------------------------------------------------------------------------
SUBSTIT_TEST='s/^(\s*(?:SET\s+)?TESTINGONLY)\s*=[ \t]*("?).*/$1=${2}FALSE$2/'

if [ "$VERBOSE" ];  then
  echo  > /dev/stderr
  debug_message "XW_APP_NAME     ='$XW_APP_NAME'"
  debug_message "SUBSTIT_TEST    ='$SUBSTIT_TEST'"
fi


#-----------------------------------------------------------------------------
#  From template, create :
#  -  The shell script to start the container,
#  -  The shell script to stop  the container,
#  -  The BAT   script to start the container,
#  -  The BAT   script to stop  the container.
#  Then insert those scripts inside XtremWeb-HEP.
#-----------------------------------------------------------------------------
APP_START_SH=""
APP_STOP_SH=""
APP_START_BAT=""
APP_STOP_BAT=""
if [ ! -z "$XW_START_CONTAINER_SH"  ] ; then 
  APP_START_SH="$XW_APP_NAME"_$(basename "$XW_START_CONTAINER_SH")
  perl  -wpe "$SUBSTIT_TEST"  "$XW_START_CONTAINER_SH"   > "$APP_START_SH"
fi
if [ ! -z "$XW_STOP_CONTAINER_SH"   ] ; then
  APP_STOP_SH="$XW_APP_NAME"_$(basename "$XW_STOP_CONTAINER_SH")
  perl  -wpe "$SUBSTIT_TEST"  "$XW_STOP_CONTAINER_SH"    > "$APP_STOP_SH"
fi
if [ ! -z "$XW_START_CONTAINER_BAT" ] ; then 
  APP_START_BAT="$XW_APP_NAME"_$(basename "$XW_START_DOCKER_BAT")
  perl  -wpe "$SUBSTIT_TEST"  "$XW_START_DOCKER_BAT"  > "$APP_START_BAT"
fi
if [ ! -z "$XW_STOP_CONTAINER_BAT"  ] ; then
  APP_STOP_BAT="$XW_APP_NAME"_$(basename "$XW_STOP_DOCKER_BAT")
  perl  -wpe "$SUBSTIT_TEST"  "$XW_STOP_DOCKER_BAT"   > "$APP_STOP_BAT"
fi


if [ -z "$DRY_RUN" ]; then
  
  [ -z "$VERBOSE" ]  ||  echo  > /dev/stderr
  
  info_message  "Inserting script files for '$XW_APP_NAME'"
  
  xw_send_file  "$APP_START_SH"
  START_SH_URI=$DATA_URI
  
  xw_send_file  "$APP_STOP_SH"
  STOP_SH_URI=$DATA_URI
  
  xw_send_file  "$APP_START_BAT"
  START_BAT_URI=$DATA_URI
  
  xw_send_file  "$APP_STOP_BAT"
  STOP_BAT_URI=$DATA_URI
  
  rm  -f  "$APP_START_SH"  "$APP_STOP_SH"  "$APP_START_BAT"  "$APP_STOP_BAT"
fi


#-----------------------------------------------------------------------------
#  Create inside XtremWeb-HEP the application without any file
#-----------------------------------------------------------------------------
[ -z "$VERBOSE" ]  ||  echo  > /dev/stderr
  
info_message  "Creating Docker application '$XW_APP_NAME'"

xw_action_success_or_clean  "$XW_SEND_APP"  $CFG  "$XW_APP_NAME"  "DOCKER"

XW_APP_UID=$(echo "$XW_MESSAGE"  |  grep '^xw://'  |  sed -e 's=^.*/==')
[ "$XW_APP_UID" ]  ||  fatal_message_clean_exit  "$XW_MESSAGE"

[ -z "$VERBOSE" ]  ||  debug_message  "XW_APP_UID='$XW_APP_UID'"

#-----------------------------------------------------------------------------
#  Create the XML file permitting to associate the files to the application
#-----------------------------------------------------------------------------
XW_APP_XML="xwhep-$XW_APP_NAME.xml"

[ -z "$VERBOSE" ]  ||  \
  ( echo  > /dev/stderr
    debug_message  "XML file for '$XW_APP_NAME' is '$XW_APP_XML'" )

grep -v '^ *$'  << END_OF_XW_APP_XML  > "$XW_APP_XML"
<xwhep>
<app>
<uid>$XW_APP_UID</uid>
<name>$XW_APP_NAME</name>
<type>$XW_APP_TYPE</type>
<launchscriptshuri>$START_SH_URI</launchscriptshuri>
<unloadscriptshuri>$STOP_SH_URI</unloadscriptshuri>
<launchscriptcmduri>$START_BAT_URI</launchscriptcmduri>
<unloadscriptcmduri>$STOP_BAT_URI</unloadscriptcmduri>
$DEFAULT_DATA_SPEC
</app>
</xwhep>
END_OF_XW_APP_XML
cat $XW_APP_XML
[ -z "$DRY_RUN" ]  ||  exit
  
#-----------------------------------------------------------------------------
#  Using the XML file, associate the files to the application
#-----------------------------------------------------------------------------
xw_action_success_or_clean  "$XW_SEND_APP"  $CFG  --xwxml "$XW_APP_XML"

# XW_APP_UID=$( echo "$XW_MESSAGE"  |  grep '^xw://' )

rm  -f  "$XW_APP_XML"

#-----------------------------------------------------------------------------
#  Display the attributes of the application just added to XtremWeb-HEP
#-----------------------------------------------------------------------------
info_message  "Application '$XW_APP_NAME' added to XtremWeb-HEP :"

xw_action_success_or_clean  "$XW_APPS"  $CFG  --xwformat xml  "$XW_APP_NAME"

perl -we '$_ = $ARGV[0];  s="\s+="\n  =g;  print'  "$XW_MESSAGE"
