#!/bin/bash
# ./deploy_workers.sh --workers=10


cd $(dirname $0)

for i in "$@"
do
case $i in
    -w=*|--workers=*)
    NB_WORKERS="${i#*=}"
    shift
    ;;
esac
done


if [[ -z $NB_WORKERS ]]; then
	echo "Empty number of workers (--workers=10)" 
	exit
fi


START=1
CURRENT_FOLDER=$PWD
HOSTNAME=`hostname`

for i in $(eval echo "{$START..$NB_WORKERS}")
do
	mkdir worker$i
	cp $CURRENT_FOLDER/docker-compose.yml worker$i/
    cp $CURRENT_FOLDER/.env worker$i/
	cd worker$i
		
	sed -i "s/WORKER_HOSTNAME=.*/WORKER_HOSTNAME=$HOSTNAME\_worker$i/g" .env
	sed -i "s/WORKER_TMPDIR=.*/WORKER_TMPDIR=\/tmp\/worker$i/g" .env
	sed -i "s/WORKERWALLETPATH=.*/WORKERWALLETPATH=..\/wallets\/wallet$i\/encrypted-wallet.json/g" .env
	
	docker-compose up -d
	cd $CURRENT_FOLDER
done



