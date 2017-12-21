#!/bin/bash -e
#=============================================================================
#  BASH script for job submission with 'xwsubmit' to XtremWeb-HEP.
#  This script really requires BASH for handling arrays.
#
#  Copyright      : 2012  E. Urbah
#                         at LAL, Univ Paris-Sud, IN2P3/CNRS, Orsay, France
#  Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by INRIA
#  Home page      : http://www.xtremweb-hep.org
#  Licence        : GPL v3, see http://www.gnu.org/licenses/
#=============================================================================
XW_INPUT_DIR=/var/www/html/3g-bridge/
BASE_URI="http://$HOSTNAME:4330/"

#-----------------------------------------------------------------------------
#  Definition of the application, arguments, input and output file.
#  This is easily customized. 
#-----------------------------------------------------------------------------
DSP_I=${1:-20}                                            #  Between 0 and 722

XW_APP_NAME=dsp
XW_APP_ARGS=(-f 22  -i "$DSP_I"  -p 723  -n pools.txt)

XW_INPUT_TYPE=' 1 local file'
XW_INPUTS=("${XW_INPUT_DIR}pools.txt")

#-----------------------------------------------------------------------------
#  Algorithm to send the job.  Modify only with greatest care.
#-----------------------------------------------------------------------------
echo
echo "Submission to XtremWeb-HEP of '$XW_APP_NAME' with$XW_INPUT_TYPE (and"  \
     "a group if available)"

XW_BIN="$(ls  -d  /opt/xwhep-client-*  |  tail  -1)/bin"

XW_MESSAGE=$("$XW_BIN/xwgroups")  ||  \
  { RC=$?;  echo "$XW_MESSAGE"  > /dev/stderr;  exit $RC; }

XW_GROUP_UID=( $(echo "$XW_MESSAGE"  |  grep  '^ *UID='  |  head  -1  |  \
                                        cut   -d "'"  -f 2) )

J=0
XW_INPUT_PARMS=()
for I in "${!XW_INPUTS[@]}"; do
  XW_INPUT_PARMS[J++]="--xwenv"
  XW_INPUT_PARMS[J++]="${XW_INPUTS[I]}"
done

#-----------------------------------------------------------------------------
#  Job submission
#-----------------------------------------------------------------------------
echo
XW_MESSAGE=$( set -x;  \
   "$XW_BIN/xwsubmit"  "$XW_APP_NAME"  "${XW_APP_ARGS[@]}"  \
                       "${XW_INPUT_PARMS[@]}"  \
                       ${XW_GROUP_UID+--xwgroup} "${XW_GROUP_UID[@]}" )  ||  \
  { RC=$?;  echo "$XW_MESSAGE"  > /dev/stderr;  exit $RC; }

XW_JOB_UID=$(echo "$XW_MESSAGE"  |  grep  '^xw://')

#-----------------------------------------------------------------------------
#  Job detailed status
#-----------------------------------------------------------------------------
echo
"$XW_BIN/xwstatus"  "$XW_JOB_UID"  --xwformat xml  |  \
  perl  -wpe  's/"\s+/"\n  /g'

echo
#-----------------------------------------------------------------------------
echo "Looping on job status.   You can interrupt the loop with (ctrl)C if"  \
     "you wish so."
#-----------------------------------------------------------------------------
XW_STATUS_OLD=''
while true; do
  sleep 5
  XW_MESSAGE=$("$XW_BIN/xwresults"  "$XW_JOB_UID")
  XW_STATUS_NEW=$(echo  "$XW_MESSAGE"  |  \
              perl  -wpe  "s/^.*STATUS='([^']*)'.*\$/\$1/")
  if [[ ( "$XW_MESSAGE"     == *ERROR*:*   ) || \
        ( "$XW_STATUS_NEW"  == "ERROR"     ) || \
        ( "$XW_STATUS_NEW"  == "COMPLETED" ) ]]; then break; fi
  if [[ "$XW_STATUS_NEW" != "$XW_STATUS_OLD" ]]; then
    echo
    echo  -n  "$XW_MESSAGE "  |  sed  -e  's/, *[A-Za-z_]*=NULL//g'
    XW_STATUS_OLD="$XW_STATUS_NEW"
  else
    echo  -n  .
  fi
done

echo
echo  "$XW_MESSAGE"
