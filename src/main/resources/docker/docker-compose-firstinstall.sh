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

# run dummy scheduler to get scripts for db
docker-compose -f $dockerComposeFile up -d scheduler

# copy scripts, conf and certificate from scheduler
docker cp iexecscheduler:/iexec/bin dbbin
docker cp iexecscheduler:/iexec/conf dbconf
docker cp iexecscheduler:/iexec/keystore/xwscheduler.pem .

# kill the dummy scheduler
docker-compose -f $dockerComposeFile down -v

# first start the database and wait a bit to have it started
docker-compose -f $dockerComposeFile up -d db
sleep 10

# copy scripts and conf in the mysql container
docker exec -i mysql mkdir scripts
docker cp dbbin mysql:/scripts/bin
docker cp dbconf mysql:/scripts/conf


# trigger the database creation in the mysql container
docker exec -i mysql /scripts/bin/setupDatabase --yes --rmdb --dbhost db
sleep 10

# remove temporary files and folders
rm -rf dbbin/
rm -rf dbconf/

# then start the scheduler and a little bit after all remaining services
docker-compose -f $dockerComposeFile up -d scheduler
sleep 80
docker-compose -f $dockerComposeFile up -d

cd $currentFolder
docker-compose -f $dockerComposeFile logs -f
