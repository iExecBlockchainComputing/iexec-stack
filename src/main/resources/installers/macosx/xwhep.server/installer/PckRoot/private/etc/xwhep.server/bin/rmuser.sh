#!/bin/bash


# Delete a user.
# Takes the account name (short name) and:
#   removes the user from all groups
#   removes the user's primary group (of the same name)
#   removes the user's account in Directory Service
#   archives and deletes the user's home directory in /Users/shortname


declare user  # to hold user's account name
declare str   # working

ROOTDIR=`dirname $0`


usage ()
{
  echo "Delete a user account, group, and group membership"
  echo "Usage: ${0##*/} username"
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
if [ $# -ne 1 ]; then
  usage
fi


user="$1"


# search Directory Service for the given user - it should exist
dscl . list /users/$user
if [ $? -ne 0 ]; then
  usage "User $user does not exist"
fi


# Delete the user from Directory Service
#
# delete the user from all groups
$ROOTDIR/del-user4group.sh all $user


# delete the user's primary group
$ROOTDIR/del-group.sh -q $user


# delete the user from Directory Service
dscl . delete /users/$user


echo "User $user deleted"


# Archive the user's home directory
#
# check that the user has a home directory
if [ -e /Users/$user ]; then
  # archive it
  cd /Users
  tar -czf ${user}-archive.tgz $user
  cd -


  # delete it CHECKING THAT AN ARCHIVE WAS CRESATED
  if [ -e /Users/${user}-archive.tgz ]; then
    rm -rf /Users/${user}/
  fi
fi


echo "User's home directory archived and deleted"


exit 0