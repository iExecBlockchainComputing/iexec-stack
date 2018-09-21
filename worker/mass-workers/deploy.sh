#!/bin/bash

# Function which checks exit status and stops execution
function checkExitStatus() {
  if [ $1 -eq 0 ]; then
    echo OK
  else
    echo $2
    exit 1
  fi
}

# This deploys a worker
deploy_worker () {

    # Test if worker entry is valid
    if [ $(echo "$1" | wc -w) -ne 6 ]; then
        echo "\"$1\" host entry is not valid."
        exit 1
    fi

    # Get params from line
    HOSTNAME=$(echo "$1" | cut -d ' ' -f 1)
    HOST=$(echo "$1" | cut -d ' ' -f 2)
    USER=$(echo "$1" | cut -d ' ' -f 3)
    SSH_KEY=$(echo "$1" | cut -d ' ' -f 4)
    SSH_PORT=$(echo "$1" | cut -d ' ' -f 5)
    WORKER_TAG=$(echo "$1" | cut -d ' ' -f 6)

    echo "Installing worker at ${HOSTNAME} (${HOST})"
    # Check if need to put shared packages
    if [ ${WORKER_TAG} != "none" ]; then
        echo "Tag ${WORKER_TAG} for worker ${HOSTNAME} was set."
        ENV=$(cat $2 | sed "s/WORKER_SHAREDPACKAGES\=.*/WORKER_SHAREDPACKAGES\=${WORKER_TAG}/")
    else
        ENV=$(cat $2)
    fi

    if [ $3 == 1 ]; then
        echo "Replacing in deploy file..."
        SCRIPT=$(cat install-worker.sh | sed -e "s/docker-compose.*up.*//" | sed "s/Docker\ compose up\...//")
    else
        SCRIPT=$(cat install-worker.sh)
    fi

    ENV=$(echo "${ENV}" | sed "s/hostname/${HOSTNAME}/")

    echo "${ENV} ${SCRIPT}" > tmp

    ssh -i ${SSH_KEY} -p ${SSH_PORT} ${USER}@${HOST} "sudo bash -s" < tmp

    rm tmp

    echo "Installing worker at ${HOSTNAME} (${HOST}) finished."

}

# All from file function
all_from_file() {

    HOSTS_NUMBER=$(( $(wc -l < $1)-1 ))
    HOSTS_INC=1

    # Loop over all hosts and reinstall worker
    while read line; do

      if [ ! ${line:0:1} == \# ]; then

        echo "------------------------ Start [${HOSTS_INC}/${HOSTS_NUMBER}] -------------------------------"
        deploy_worker "$line" $2 $3
        echo "------------------------ End [${HOSTS_INC}/${HOSTS_NUMBER}] -------------------------------"

        HOSTS_INC=$(( HOSTS_INC+1 ))

      fi

    done < $1

}

# Usage
usage() {
    echo "Command usage: "
    echo "./deploy.sh env_file [--all-from-file|--line-from-file] file_name line_number [--stop-only]"
    exit 1
}

# Check if at least 2 params
if [ "$#" -lt 3 ]; then
    usage
fi

# Check if files exists
if [ ! -f $1 ]; then
    echo "File $1 not found!"
    exit 1
fi

if [ ! -f $3 ]; then
    echo "File $3 not found!"
    exit 1
fi


if [ "$2" == "--all-from-file" ]; then

    if [ "$4" == "--stop-only" ]; then
        echo "Stopping only..."
        all_from_file $3 $1 1
    else
        all_from_file $3 $1 0
    fi

elif [ "$2" == "--line-from-file" ]; then

    if [ "$#" -lt 4 ]; then
        usage
    fi

    LINE_PARAM=$(( $4+1 ))
    LINE=$(sed "${LINE_PARAM}!d" $3)
    FILE_LINES=$(( $(wc -l < $3)-1 ))

    if [ $4 -gt 0 ] && [ $4 -le $FILE_LINES ]; then

        if [ "$5" == "--stop-only" ]; then
            echo "Stopping only..."
            deploy_worker "$LINE" $1 1
        else
            deploy_worker "$LINE" $1 0
        fi

    else
        echo "Wrong file line number. Must be greater that 0 and less then ${FILE_LINES}."
    fi

else
    usage
fi
