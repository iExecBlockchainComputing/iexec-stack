#!/bin/bash


# Delete a group.
# Takes the group name and deletes it from Directory Service


declare quiet="no"  # -q option not specified
declare group       # hold te given group name
declare gid         # hold the group id derived from the group name 
declare ans         # reply from prompt


usage ()
{
  echo "Delete a group"
  echo "Usage: ${0##*/} [-q] groupname"
  echo "       -q - quiet: no warnings or prompts for confirmation" 
  echo "            otherwise a warning is issued if the group to"
  echo "            be deleted is a user's primary group"
  if [ "$*" != "" ]; then echo; echo "Error: $*"; fi
  exit 1
}




# The script must be run as root 
#
if [ "$USER" != "root" ]; then
  echo "Must be run as root"
  exit 1
fi




# Check parameters
#
if [ "$1" = "-q" ]; then
  quiet="yes"
  shift
fi


if [ $# -ne 1 ]; then
  usage
fi


group="$1"

if [ "$group" = "" ]; then exit; fi


# search Directory Service for the given group - it should exist
dscl . list /groups/$group
if [ $? -ne 0 ]; then
  usage "Group $group does not exist"
fi




# Check if this is a primary group for some user and warn if so
#   but not in quiet mode
if [ $quiet = "no" ]; then


  # get the group number from the name
#  gid="$(nireport . /groups name gid | grep -w "^$group" | cut -f 2)"
  gid="$(dscl . read /groups/$group | grep PrimaryGroupID | cut -f 2 -d ' ')"

  for i in `dscl . list /users` ; do
      str="$(dscl . read /users/$i | grep PrimaryGroupID | grep $gid)"
      if [ ! -z "$str" ]; then
	  echo "WARNING: $group is a primary group for user $i:"
#	  read -p "Type a to abort: " ans
#	  if [ "$ans" = "a" ]; then
	      echo "Aborted"
	      exit
#	  fi
      fi
  done
fi


# Delete the group from Directory Service
#
# sanity check
dscl . delete /groups/$group


echo "Group $group deleted"
exit 0
