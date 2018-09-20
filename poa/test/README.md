# prerequiste

```
sudo su
wget https://raw.githubusercontent.com/branciard/blockchain-dev-env/master/bootstrap-ubuntu-aws.sh
chmod +x bootstrap-ubuntu-aws.sh
./bootstrap-ubuntu-aws.sh
```

# git clone
```
su - ubuntu
git clone https://github.com/iExecBlockchainComputing/iexec-deploy.git
cd iexec-deploy/poa/test/
chmod +x bootpoatestnet.sh 
./bootpoatestnet.sh --name tototest --nodes 2

```
exemple creation homechain et foreign chain to test bridge between 2 poa
```
./bootpoatestnet.sh --name homechain --nodes 1
./bootpoatestnet.sh --name foreignchain --nodes 1

```

# script usage
```
"bootpoatestnet.sh OPTIONS
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
```
# multiple instances aws test chain deploiement :

deploy on multiple instance :
-  generate  config  on one server and zip it. zip parity-deploy.
- copy all parity-deploy.zip on all instances.
- open 30303 port on aws nodes instances
- check and replace public IP information in parity-deploy/deployment/chain/reserved_peers
- remove in docker-compose to start only nodes you want (host1, host2 etc ..)
- docker-compose up again on all servers and check thaht you see peers connected to each others. peers =! 0 in logs.
