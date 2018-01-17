#!/bin/bash

docker-compose up -d db
sleep 20

docker exec -it mysql mkdir scripts
docker cp /home/ugo/iexecdev/xtremweb-hep/build/dist/xtremweb-12.2.3-SNAPSHOT/bin mysql:/scripts/
docker cp /home/ugo/iexecdev/xtremweb-hep/build/dist/xtremweb-12.2.3-SNAPSHOT/conf  mysql:/scripts/
docker exec -it mysql /scripts/bin/setupDatabase --yes --rmdb

docker-compose up -d scheduler
sleep 10
docker-compose up -d worker
