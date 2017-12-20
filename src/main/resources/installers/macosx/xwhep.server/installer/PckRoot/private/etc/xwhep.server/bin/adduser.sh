
#!/bin/bash


# Create a user.
# Takes the user's firstname (=shortname), lastname, uid, and staff|admin
# and creates:
#   a new user in Directory Service
#   a new /Users/firstname home directory


usage ()
{
  echo "Create a new staff or admin user"
  echo "Usage: ${0##*/} firstname lastname uid staff|admin"
  if [ "$*" != "" ]; then echo "  Error: $*"; fi
  exit 1
}




# The script must be run by root
#
if [ "$USER" != "root" ]; then
  echo "Must be run as root."
  exit 1
fi




# Check parameters
#
if [ $# -ne 4 ]; then
  usage
fi


first=$1; last=$2; uid=$3; accnt=$4


# check that the users does not already have a home directory
if [ -e /Users/$first ]; then
  usage "User $first already exists at /Users/$first"
fi


# search Directory Service for the given user - it should not exist
dscl . list /users/$first
if [ $? -eq 0 ]; then
  usage "User $first already exists (but does not have a home directory)"
fi


# search Directory Service for the given uid - it should not exist
id -u $uid
if [ $? -eq 0 ]; then
  usage "User ID $uid already exists"
fi


# search Directory Service for the given group - it should not exist
dscl . list /groups/$first
if [ $? -eq 0 ]; then
  usage "Group $first already exists"
fi


# search Directory Service for the given gid - it should not exist
id -g $uid
if [ ! -z "$str" ]; then
    usage "Group ID $uid already exists"
fi


# ensure either staff or admin is given
if [ $4 != staff ] && [ $4 != admin ]; then
  usage "Give account type as 'staff' or 'admin'"
fi



#password="xwworkermgr"

# Add the new user to Directory Service
#
# add user and essential properties
dscl . create /users/$first
dscl . create /users/$first name $first
dscl . create /users/$first passwd "*"
dscl . create /users/$first hint "This user aims to execute XWHEP scripts"
dscl . create /users/$first uid $uid
dscl . create /users/$first gid $uid
dscl . create /users/$first home /Users/$first
dscl . create /users/$first shell /bin/bash
dscl . create /users/$first realname "$first $last"
dscl . create /users/$first picture "/Library/User Pictures/Fun/Smack.tif"
dscl . create /users/$first sharedDir Public


# add some other properties that are usually in Directory Service
dscl . create /users/$first _shadow_passwd ""
dscl . create /users/$first _writers_hint $first
dscl . create /users/$first _writers_real_name $first


# add the new group
dscl . create /groups/$first
dscl . create /groups/$first name $first
dscl . create /groups/$first passwd "*"
dscl . create /groups/$first gid $uid


echo "New user and group $first created"




# Add admin users to the admin group
#
if [ $4 = admin ]; then 
  dscl . merge /groups/admin users $first 
  dscl . merge /groups/appserverusr users $first 
  dscl . merge /groups/appserveradm users $first 
  echo "$first added to groups admin, appserverusr, appserveradm"
fi




# Create the home directory, populate from the template, and set owners
#
mkdir /Users/$first
if [ ! -d /Users/$first ]; then
  echo "Unable to create the user's home directory /Users/$first"
  exit
fi


ditto -rsrc /System/Library/User\ Template/English.lproj/ /Users/$first
chown -R ${first}:$first /Users/$first
echo "Home directory /Users/$first created and populated"




# Now give the user a password
#
#echo "A password for this account must be given, it is currently blank"
#passwd `echo $password`


exit 0
