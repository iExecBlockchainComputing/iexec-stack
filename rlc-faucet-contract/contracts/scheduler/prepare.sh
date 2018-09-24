#!/bin/bash

if [ "$1" == "--generate-passwords" ]; then

    echo "Generating passwords..."
    sudo apt-get install -y pwgen

    ADMINPASSWORD=$(pwgen 20 1)
    WORKERPASSWORD=$(pwgen 20 1)
    VWORKERPASSWORD=$(pwgen 20 1)
    MYSQL_ADMIN_PASSWORD=$(pwgen 20 1)
    MYSQL_USER_PASSWORD=$(pwgen 20 1)
    GRAFANA_SQL_PASSWORD=$(pwgen 20 1)

    echo "Replacing passwords in variables file..."
    sed -i "s/ADMINPASSWORD\=.*/ADMINPASSWORD=$ADMINPASSWORD/" ./variables
    sed -i "s/WORKERPASSWORD\=.*/WORKERPASSWORD=$WORKERPASSWORD/" ./variables
    sed -i "s/VWORKERPASSWORD\=.*/VWORKERPASSWORD=$VWORKERPASSWORD/" ./variables
    sed -i "s/MYSQL_ADMIN_PASSWORD\=.*/MYSQL_ADMIN_PASSWORD=$MYSQL_ADMIN_PASSWORD/" ./variables
    sed -i "s/MYSQL_USER_PASSWORD\=.*/MYSQL_USER_PASSWORD=$MYSQL_USER_PASSWORD/" ./variables
    sed -i "s/GRAFANA_SQL_PASSWORD\=.*/GRAFANA_SQL_PASSWORD=$GRAFANA_SQL_PASSWORD/" ./variables
    
else 
    source ./variables
fi

cp ./database/scheduler.sql ./scheduler/database/1_scheduler.sql
cp ./database/grafana.sql ./scheduler/database/2_grafana.sql

echo "Replacing info in scheduler.sql file..."
sed -i "s/'workerp'/'$WORKERPASSWORD'/" ./scheduler/database/1_scheduler.sql
sed -i "s/'adminp'/'$ADMINPASSWORD'/" ./scheduler/database/1_scheduler.sql
sed -i "s/'vworkerp'/'$VWORKERPASSWORD'/" ./scheduler/database/1_scheduler.sql

echo "Replacing info in grafana.sql file..."
sed -i "s/grafanapassword/$GRAFANA_SQL_PASSWORD/" ./scheduler/database/2_grafana.sql

echo "Scheduler is ready to be deployed..."
