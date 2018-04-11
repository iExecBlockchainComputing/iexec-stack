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
#  GLOBAL VARIABLES set as return values by functions
#-----------------------------------------------------------------------------
XW_MESSAGE=''                 #  Set by function  xw_action_success_or_clean
XW_ATTRIBUTE_VALUE=''         #  Set by function  extract_xw_attribute_value

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

if [ "$VERBOSE" ];  then
  debug_message "DEFAULT_DATA_URI='$DEFAULT_DATA_URI'"
  echo  > /dev/stderr
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
#  Create inside XtremWeb-HEP the application without any file
#-----------------------------------------------------------------------------
[ -z "$VERBOSE" ]  ||  echo  > /dev/stderr
  
info_message  "Creating Docker application '$XW_APP_NAME'"

xw_action_success_or_clean  "$XW_SEND_APP"  $CFG  "$XW_APP_NAME"  "DOCKER"

XW_APP_UID=$(echo "$XW_MESSAGE"  |  grep '^xw://'  |  sed -e 's=^.*/==')
[ "$XW_APP_UID" ]  ||  fatal_message_clean_exit  "$XW_MESSAGE"


#-----------------------------------------------------------------------------
#  Display the attributes of the application just added to XtremWeb-HEP
#-----------------------------------------------------------------------------
info_message  "Application '$XW_APP_NAME' added to XtremWeb-HEP :"

xw_action_success_or_clean  "$XW_APPS"  $CFG  --xwformat xml  "$XW_APP_NAME"

perl -we '$_ = $ARGV[0];  s="\s+="\n  =g;  print'  "$XW_MESSAGE"
