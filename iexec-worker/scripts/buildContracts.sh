#!/usr/bin/env bash

set -e
set -o pipefail

baseDir="../src/main/resources/solidity"

targets="
poco/WorkerPool
poco/IexecHub
poco/WorkOrder
poco/Marketplace
poco/rlc-token/contracts/RLC
"

currentdir=`pwd`
echo $(pwd)

for target in ${targets}; do
    dirName=$(dirname "${target}")
    fileName=$(basename "${target}")

    cd $baseDir
    echo "Compiling Solidity file ${target}.sol"
    mkdir -p $dirName/build

    solc --bin --abi --optimize --overwrite \
            --allow-paths "$(pwd)" \
            ${dirName}/${fileName}.sol -o ${dirName}/build/
    echo "Complete"

    echo "Generating contract bindings"
    $currentdir/../extlib/web3j-3.2.0/bin/web3j solidity generate \
        ${dirName}/build/${fileName}.bin \
        ${dirName}/build/${fileName}.abi \
        -p com.iexec.worker.contracts.generated \
        -o ../../java/ > /dev/null
    echo "Complete"

    cd -
done
