#!/bin/bash

currentFolder=`pwd`
scriptPath=`readlink -f $0`
folderPath=`dirname $scriptPath`
dockerComposeFile=$folderPath"/docker-compose.yml"


cd $folderPath

# run dummy scheduler to get scripts for db
docker-compose -f $dockerComposeFile up -d scheduler

# copy scripts, conf and certificate from scheduler
docker cp xwscheduler:/xwhep/bin dbbin
docker cp xwscheduler:/xwhep/conf dbconf
docker cp xwscheduler:/xwhep/keystore/xwscheduler.pem .

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
sleep 10
docker-compose -f $dockerComposeFile up -d

cd currentFolder
docker-compose -f $dockerComposeFile logs -f
