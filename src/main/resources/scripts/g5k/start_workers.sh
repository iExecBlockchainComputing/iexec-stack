#!/bin/sh

#G5K user name
USER=ollodygensky
#Maximum number of job
MAX_WORKER=30
#Maximum number of job waiting to be submitted
MAX_WAITING=7
#Sleep between each submission in seconds
SLEEP_DUR=240
#Wall time in hour
WALLTIME=6:00:00


while [ -f start_workers.run ]
do

N_WORKER=`oarstat -u $USER | grep $USER | wc -l`
N_WAITING=`oarstat -u $USER | grep $USER | awk '{print $(NF-1)}' | grep W | wc -l`
echo "At `date`, $N_WORKER workers exist, $N_WAITING are waiting submission"
if [ $N_WORKER -lt $MAX_WORKER ] 
then 
    if [ $N_WAITING -lt $MAX_WAITING ]
    then
	echo "Creating new worker..."
	oarsub -t allow_classic_ssh -t besteffort -l walltime=$WALLTIME ./launch_xw_worker_unix_and_darwin.sh
    else
	echo "Too many workers are waiting already"
    fi
else
    echo "Maximum number of workers already existing"
fi

echo "Going to sleep for $SLEEP_DUR seconds..."
sleep $SLEEP_DUR
rm -f OAR*
done

echo "------"
