#!/bin/bash

NB_WORKERS=20


START=1
CURRENT_FOLDER=$PWD

for i in $(eval echo "{$START..$NB_WORKERS}")
do
	cd workers/worker$i
	docker-compose down
	cd $CURRENT_FOLDER
done
