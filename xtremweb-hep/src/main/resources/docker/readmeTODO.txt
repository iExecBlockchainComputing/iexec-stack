How to use docker-compose:

1. In xwconfigure.values change the value of DBHOST to db (that's the name of the service in docker compose)
2. build images using: gradle buildAll generateKeys buildImages
3. Run docker-compose but only for the db (docker-compose up db) and run the following command:
docker exec -it mysql mkdir scripts && \
docker cp /PATH/TO/BUILD/bin mysql:/scripts/ && \
docker cp /PATH/TO/BUILD/conf  mysql:/scripts/ && \
docker exec -it mysql /scripts/bin/setupDatabase --yes --rmdb

4. Then run docker-compose up to start the other containers.

5. if you want to start a client, the best way to do it is to start a separate client container and connect to the network use by the docker-compose. It can be done with the following command:
- docker run -it --env XWSERVERADDR="scheduler" --env XWSERVERNAME="xtrscheduler" -v /PATH/TO/CERTIFICATE/xwscheduler.pem:/xwhep/certificate/xwscheduler.pem --network=xtremweb  xtremweb/client:12.2.3-SNAPSHOT
