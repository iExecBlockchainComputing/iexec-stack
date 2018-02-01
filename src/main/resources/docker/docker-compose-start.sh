#!/bin/bash

# run dummy scheduler to get scripts for db
docker-compose up -d scheduler

# copy scripts, conf and certificate from scheduler
docker cp xwscheduler:/xwhep/bin dbbin
docker cp xwscheduler:/xwhep/conf dbconf
docker cp xwscheduler:/xwhep/keystore/xwhepcert.pem .

# kill the dummy scheduler
docker-compose down -v

# first start the database and wait a bit to have it started
docker-compose up -d db
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
docker-compose up -d scheduler
sleep 10
docker-compose up -d

docker-compose logs -f
