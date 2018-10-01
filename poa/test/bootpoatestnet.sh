#!/bin/sh


help()  {

  echo "bootpoatestnet.sh OPTIONS
Usage:
REQUIRED:
        --name    : blockchainName
        --nodes   : number_of_nodes
OPTIONAL:
        --stepDuration : delay between 2 blocks. Default: 2 sec
        --wallets : nb wallet create. Default: 10
        --eth     : eth amount (in wei) given to wallets Default: 10 ETH
        --rlc     : rlc amount (in nRLC) given to wallets Default: RLC ETH
        -h | --help
  "

}


isInteger() {
  if test ${1} -eq ${1} 2>/dev/null; then
    return 0
  fi
  return 1
}

#### MAIN

#default value
CHAIN_NAME=""
CHAIN_NODES=""
STEP_DURATION=2
PASSWORD_LENGTH=24
NB_WALLETS=10
ETH_AMOUNT=10000000000000000000
RLC_AMOUNT=10000000000


ARGS="$@"

if [ $# -lt 2 ]
then
  echo "2 REQUIRED arguments needed"
  help
  exit 1
fi

while [ "$1" != "" ]; do
  case $1 in
    --name )          shift
      CHAIN_NAME=$1
      ;;
    --nodes )          shift
      CHAIN_NODES=$1
      ;;
    --wallets )           shift
      NB_WALLETS=$1
      ;;
    --stepDuration )           shift
      STEP_DURATION=$1
      ;;
    --eth )                   shift
      ETH_AMOUNT=$1
      ;;
    --rlc )                   shift
      RLC_AMOUNT=$1
      ;;
    -h | --help )           help
      exit
      ;;
  esac
  shift
done



#check mandatory
if [ -z $CHAIN_NAME ] ; then
  echo "--name  arg is mandatory"
  help
  exit 1
fi

if [ -z $CHAIN_NODES ] ; then
  echo "--nodes  arg is mandatory"
  help
  exit 1
fi

#check ${CHAIN_NODES} integer
isInteger ${CHAIN_NODES}
if [ $? -eq 1 ]
then
  echo "CHAIN_NODES ${CHAIN_NODES} must be an integer."
  exit 1
fi

#check ${NB_WALLETS} integer
isInteger ${NB_WALLETS}
if [ $? -eq 1 ]
then
  echo "NB_WALLETS ${NB_WALLETS} must be an integer."
  exit 1
fi

isInteger ${STEP_DURATION}
if [ $? -eq 1 ]
then
  echo "STEP_DURATION ${STEP_DURATION} must be an integer."
  exit 1
fi


echo "git clone ..."
git clone https://github.com/iExecBlockchainComputing/wallets.git
git clone https://github.com/iExecBlockchainComputing/PoCo.git

git clone https://github.com/branciard/parity-deploy.git
cd parity-deploy
git checkout dev
echo "generate pwd"

./config/utils/pwdgen.sh -n ${CHAIN_NODES} -l ${PASSWORD_LENGTH}
if [ $? -eq 1 ]
then
  echo "pwdgen.sh  script failed"
  exit 1
fi

echo "call parity-deploy script"

./parity-deploy.sh --config aura --name ${CHAIN_NAME} --nodes ${CHAIN_NODES} --ethstats --expose


sed -i 's/0x00Ea169ce7e0992960D3BdE6F5D539C955316432/0x0513425AE000f5bAEaD0ed485ED8c36E737e3586/g' deployment/chain/spec.json
sed -i "s/\"stepDuration\": \"2\"/\"stepDuration\": \"`echo $STEP_DURATION`\"/g" deployment/chain/spec.json



echo "docker-compose up -d ..."
docker-compose up -d

cd -



cd PoCo
npm i
npm install truffle-hdwallet-provider-privkey
# create only one category
sed -i 's/0/4/g' migrations/2_deploy_contracts.js
#copy existing truffle.js
cp truffle.js truffle.ori

PKEY=$(cat ../wallets/richman/wallet.json | grep privateKey | cut -d ":" -f2 | cut -d "," -f1)

sed "s/__PRIVATE_KEY__/${PKEY}/g" ../truffle.tmpl > truffle.js
sed -i 's/0x//' truffle.js

echo "launch truffle migrate"
echo "">nohup.out
nohup ./node_modules/.bin/truffle migrate --reset --network richman &
echo $! > save_pid.txt
tail -f nohup.out | sed '/^m_categoriesCount is now: 1$/ q'
sleep 10c
cat nohup.out
pidmigrate=$(cat save_pid.txt)
kill -9 ${pidmigrate}
echo "truffle migrate killed"

IexecHubAddress=$(cat nohup.out | grep "IexecHub deployed at address:" | cut -d":" -f2 | cut -d" " -f2)

cd -

cd wallets
# set the right IexecHub find in PoCo/build/contracts/IexecHub.json contract address
sed -i "s/0xc4e4a08bf4c6fd11028b714038846006e27d7be8/${IexecHubAddress}/g" richman/chain.json
sed -i 's/1337/17/g' richman/chain.json
./topUpWallets --from=1 --to=${NB_WALLETS} --minETH=${ETH_AMOUNT} --maxETH=${ETH_AMOUNT} --chain=dev --minRLC=${RLC_AMOUNT}

echo "POA test chain ${CHAIN_NAME} is installed and up "
