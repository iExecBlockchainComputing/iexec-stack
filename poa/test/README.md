# prerequiste

```
sudo su
wget https://raw.githubusercontent.com/branciard/blockchain-dev-env/master/bootstrap-ubuntu-aws.sh
chmod +x bootstrap-ubuntu-aws.sh
./bootstrap-ubuntu-aws.sh
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
