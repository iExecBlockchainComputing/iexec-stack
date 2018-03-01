#!/bin/bash

currentFolder=`pwd`
scriptPath=`readlink -f $0`
[ $? -ne 0 ] && scriptPath=$(basename $0)
folderPath=`dirname $scriptPath`
dockerComposeFile=$folderPath"/docker-compose.yml"


#### find an availabe name of folder for the worker ####
FOLDER_NAME="/tmp/worker"
RESULTS_FOLDER=""
CAN_CREATE_NEW_FOLDER=false
NUMBER_MAX_TRY=100

for i in  $(eval echo "{1..$NUMBER_MAX_TRY}")
do
  if [ ! -d "$FOLDER_NAME$i" ]; then
    RESULTS_FOLDER="$FOLDER_NAME$i"
    CAN_CREATE_NEW_FOLDER=true
    break
  fi
done

if [ "$CAN_CREATE_NEW_FOLDER" == false ]; then
  echo "There are more than $NUMBER_MAX_TRY folders /tmp/worker# on the machine, please delete them if they are unused."
fi

# change the value in the .env file
sed -i "s/RESULTS_FOLDER=.*/RESULTS_FOLDER=${RESULTS_FOLDER//\//\\/}/g" .env

########################################################

cd $folderPath
docker-compose -f $dockerComposeFile up -d db
sleep 5
docker-compose -f $dockerComposeFile up -d scheduler
sleep 10
docker-compose -f $dockerComposeFile up -d
cd $currentFolder

docker-compose -f $dockerComposeFile logs -f
