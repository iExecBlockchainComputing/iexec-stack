#!/bin/bash

# first start the database and wait a bit to have it started
docker-compose up -d db
sleep 10

PWD="$(pwd)"

# copy scripts and conf in the mysql container
docker exec -it mysql mkdir scripts
docker cp $PWD/../bin mysql:/scripts/
docker cp $PWD/../conf mysql:/scripts/
docker exec -it mysql /scripts/bin/setupDatabase --yes --rmdb
sleep 10

# then start the scheduler and a little bit after all remaining services
docker-compose up -d scheduler
sleep 10
docker-compose up -d

docker-compose logs -f