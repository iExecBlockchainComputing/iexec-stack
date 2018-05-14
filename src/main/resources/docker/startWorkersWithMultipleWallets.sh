#!/bin/bash


# read a file called wallets.list line by line then call the script startWorkerWithSingleWallet.sh
# the wallets.list file is made up lines as follow:
# WALLET_PATH WALLET_PASSWORD

COUNTER=0

while IFS='' read -r line || [[ -n "$line" ]]; do
    WALLET_PATH=`echo $line | awk -F ' ' '{print $1}'`
    WALLET_PASSWORD=`echo $line | awk -F ' ' '{print $2}'`
    COUNTER=$[$COUNTER +1]
    WORKER_NAME=worker_$COUNTER

    ./startWorkerWithSingleWallet.sh $WORKER_NAME $WALLET_PATH $WALLET_PASSWORD
done < wallets.list
