#!/bin/bash

# ./deploy_remote.sh --file=deployment

HOME_FOLDER=$PWD
DEST_FOLDER="/home/ubuntu/deployWorkers"

deploy_from_file() {

	mkdir -p $HOME_FOLDER/deployWorkers 
    # Loop over all hosts and deploy workers
    while read line; do

      if [ ! ${line:0:1} == \# ]; then

        echo "------------------------ Start [$line] -------------------------------"
        deploy_workers "$line"
        echo "------------------------ End [$line] -------------------------------"

        HOSTS_INC=$(( HOSTS_INC+1 ))

      fi

    done < $1

}

deploy_workers() {
	cd $HOME_FOLDER

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
	echo "sudo rm -rf $DEST_FOLDER && mkdir -p $DEST_FOLDER && sudo chown -R ubuntu:ubuntu $DEST_FOLDER && sudo chmod -R 775 $DEST_FOLDER"  > tmp 
	ssh -o "StrictHostKeyChecking no" -i $SSH_KEY -p $SSH_PORT $REMOTE_HOST "sudo bash -s" < tmp
	
	# prepare all files locally
	cd $HOME_FOLDER/deployWorkers
	mkdir $HOSTNAME;
	CURRENT_FOLDER=$HOME_FOLDER"/deployWorkers/"$HOSTNAME"/"
	cp $HOME_FOLDER/start_workers.sh $CURRENT_FOLDER	

	# Loop on all workers to create all repos
	for i in $(eval echo "{$FROM_WORKER..$TO_WORKER}")
	do
		cd $CURRENT_FOLDER
		
		#copy wallet
		mkdir -p $CURRENT_FOLDER/wallets/wallet$i
		cp -r $HOME_FOLDER/wallets/wallet$i/ $CURRENT_FOLDER/wallets

		# create the worker repo
		mkdir -p workers/worker$i
		cp $HOME_FOLDER/docker-compose.yml workers/worker$i/
		cp $HOME_FOLDER/.env.template workers/worker$i/.env
		cd workers/worker$i
		sed -i "s/WORKER_HOSTNAME=.*/WORKER_HOSTNAME=$HOSTNAME\_worker$i/g" .env
		sed -i "s/WORKER_TMPDIR=.*/WORKER_TMPDIR=\/tmp\/worker$i/g" .env
		sed -i "s/WORKERWALLETPATH=.*/WORKERWALLETPATH=..\/..\/wallets\/wallet$i\/encrypted-wallet.json/g" .env
	done

	# zip the content
	cd $HOME_FOLDER"/deployWorkers/"
	tar -cvf $HOSTNAME.tar $HOSTNAME/*

	# send it to the machine where the workers will run
	scp -i $SSH_KEY -r $HOME_FOLDER"/deployWorkers/"$HOSTNAME.tar $REMOTE_HOST:$DEST_FOLDER/$HOSTNAME.tar

	# start the docker compose for that worker
	echo "cd $DEST_FOLDER && tar -xvf $HOSTNAME.tar && cd $HOSTNAME && ./start_workers.sh --start --from=$FROM_WORKER --to=$TO_WORKER " > tmp
	ssh -o "StrictHostKeyChecking no" -i $SSH_KEY -p $SSH_PORT $REMOTE_HOST "sudo bash -s" < tmp
	
	rm tmp
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


