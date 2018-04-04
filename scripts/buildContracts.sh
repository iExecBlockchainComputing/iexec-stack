#!/usr/bin/env bash

set -e
set -o pipefail

targets="
poco/IexecHub
poco/WorkerPool
poco/AuthorizedList
poco/Marketplace
poco/rlc-token/contracts/RLC
"
# TODO - try to generate enums from here
# Compiling Solidity file poco/IexecLib.sol Complete
# Generating contract bindings              Unable to parse input ABI file

currentdir=`pwd`
echo $(pwd)

baseDir=$currentdir"/../src/main/resources/solidity"
echo $baseDir

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
        -p com.iexec.scheduler.contracts.generated \
        -o ../../java/ > /dev/null
    echo "Complete"

    cd -
done
