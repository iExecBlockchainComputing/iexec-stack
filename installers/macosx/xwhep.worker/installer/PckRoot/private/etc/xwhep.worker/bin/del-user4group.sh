#!/bin/bash


# Remove a user from a group, or all groups
# Removes an existing user from an existing group in NetInfo, or from
#  all groups that user belongs to (but not their primary group)


declare groups  # hold the given group name or the list of groups
declare user    # hold the user account name
declare gid     # hold the group id derived from the group name
declare str strgroup stringroup strprimary  # working




usage ()
{
  echo "Remove a user from a group or all groups"
  echo "Usage: ${0##*/} group|all user"
  echo "       for 'all' the user is removed from all but their primary group"
  if [ "$*" != "" ]; then echo; echo "Error: $*"; fi
  exit 1
}




# Ensure user is root
#
if [ "$USER" != "root" ]; then
  echo "Must be run as root"
  exit 1
fi




# Check parameters
#
if [ $# -lt 2 ]; then
  usage
fi


groups="$1"; user="$2"

#
# If group is all, expand into the list of groups the user belongs to
#
if [ $groups = "all" ]; then
  groups="$(id -Gnr $user)"
  [ $? -ne 0 ] && echo "Unknown user $user" && exit
fi

#
# Loop to remove the user from each group
#
for group in $groups; do

# check if the group exists
    dscl read . /group/$group > /dev/null 2>&1
    if [ $? -ne 0 ]; then
	echo "Group $group does not exist"
	exit
    fi

# get the group number from the name
      gid="$(dscl . read /groups/$group | grep PrimaryGroupID | cut -f 2 -d ' ')"

# check if this is the user's primary group
      dscl . read /users/$i | grep PrimaryGroupID | grep $gid > /dev/null 2>&1
      if [ $? -ne 0 ]; then
	  echo "User $user not in group $group"
	  exit
      fi

      dscl . delete /groups/$group users $user
      echo "User $user removed from group $group"
done

exit 0
