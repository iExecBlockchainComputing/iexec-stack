#!/bin/bash

# ./deploy.sh --file=deployment

DEST_FOLDER="/home/ubuntu/testdeploy"
CURRENT_FOLDER=$PWD

deploy_from_file() {

    # Loop over all hosts and deploy workers
    while read line; do

      if [ ! ${line:0:1} == \# ]; then

        echo "------------------------ Start [$line] -------------------------------"
        deploy_workers "$line" #$2 $3
        echo "------------------------ End [$line] -------------------------------"

        HOSTS_INC=$(( HOSTS_INC+1 ))

      fi

    done < $1

}

deploy_workers() {
	# Get params from line
    HOSTNAME=$(echo "$1" | cut -d ' ' -f 1)
    HOST=$(echo "$1" | cut -d ' ' -f 2)
    USER=$(echo "$1" | cut -d ' ' -f 3)
    SSH_KEY=$(echo "$1" | cut -d ' ' -f 4)
    SSH_PORT=$(echo "$1" | cut -d ' ' -f 5)
    WORKER_TAG=$(echo "$1" | cut -d ' ' -f 6)
	FROM_WORKER=$(echo "$1" | cut -d ' ' -f 7)
	TO_WORKER=$(echo "$1" | cut -d ' ' -f 8)
	
	REMOTE_HOST=$USER@$HOST

	# Remove previous stuff
	echo "sudo rm -rf $DEST_FOLDER"  > tmp 
	ssh -i $SSH_KEY -p $SSH_PORT $REMOTE_HOST "sudo bash -s" < tmp

	# Loop 
	for i in $(eval echo "{$FROM_WORKER..$TO_WORKER}")
	do
		# create folders with correct rights		
		echo "mkdir -p $DEST_FOLDER/workers/worker$i && sudo chown -R ubuntu:ubuntu $DEST_FOLDER && sudo chmod -R 775 $DEST_FOLDER"  > tmp 
		ssh -i $SSH_KEY -p $SSH_PORT $REMOTE_HOST "sudo bash -s" < tmp

		# copy docker compose and .env files		
		cp $CURRENT_FOLDER/.env.template $CURRENT_FOLDER/.env
		sed -i "s/WORKER_HOSTNAME=.*/WORKER_HOSTNAME=$HOSTNAME\_worker$i/g" .env
		sed -i "s/WORKER_TMPDIR=.*/WORKER_TMPDIR=\/tmp\/worker$i/g" .env
		sed -i "s/WORKERWALLETPATH=.*/WORKERWALLETPATH=\/home\/ubuntu\/wallets\/wallets\/wallet$i\/encrypted-wallet.json/g" .env
		scp -i $SSH_KEY $CURRENT_FOLDER/docker-compose.yml $CURRENT_FOLDER/.env $REMOTE_HOST:$DEST_FOLDER/workers/worker$i

		# start the docker compose for that worker
		echo "cd $DEST_FOLDER/workers/worker$i && docker-compose up -d" > tmp
		ssh -i $SSH_KEY -p $SSH_PORT $REMOTE_HOST "sudo bash -s" < tmp
	done
}



for i in "$@"
do
case $i in
    --file=*)
    FILE="${i#*=}"
    shift
    ;;

	--stop)
    STOP_WORKERS=true
    shift
    ;;
esac
done

if [ "$STOP_WORKERS" = true ] ; then
    stop_workers
fi

deploy_from_file $FILE


