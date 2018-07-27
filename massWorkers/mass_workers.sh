#!/bin/bash
# ./mass_workers.sh --workers=10 --start/--stop

START_WORKERS=false
STOP_WORKERS=false


cd $(dirname $0)

for i in "$@"
do
case $i in
    -w=*|--workers=*)
    NB_WORKERS="${i#*=}"
    shift
    ;;
	
	--start)
    START_WORKERS=true
    shift
    ;;

	--stop)
    STOP_WORKERS=true
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


start_workers () {
	for i in $(eval echo "{$START..$NB_WORKERS}")
	do
		mkdir -p workers/worker$i
		cp $CURRENT_FOLDER/docker-compose.yml workers/worker$i/
		cp $CURRENT_FOLDER/.env workers/worker$i/
		cd workers/worker$i
			
		sed -i "s/WORKER_HOSTNAME=.*/WORKER_HOSTNAME=$HOSTNAME\_worker$i/g" .env
		sed -i "s/WORKER_TMPDIR=.*/WORKER_TMPDIR=\/tmp\/worker$i/g" .env
		sed -i "s/WORKERWALLETPATH=.*/WORKERWALLETPATH=..\/..\/wallets\/wallet$i\/encrypted-wallet.json/g" .env
		
		docker-compose up -d
		cd $CURRENT_FOLDER
	done
}

stop_workers () {
	for i in $(eval echo "{$START..$NB_WORKERS}")
	do
		cd workers/worker$i
		docker-compose down
		cd $CURRENT_FOLDER
	done
}


if [ "$START_WORKERS" = true ] ; then
    start_workers
fi

if [ "$STOP_WORKERS" = true ] ; then
    stop_workers
fi

