# iExec Scheduler (WorkerPool)

### Clone project with it submodules (PoCo & rlc-token)


```
git clone --recurse-submodules https://github.com/iExecBlockchainComputing/iexec-scheduler.git          *
```
or ```git clone https://github.com/iExecBlockchainComputing/iexec-scheduler.git   && cd iexec-scheduler && git submodule update --init --recursive```
```
cd iexec-scheduler/Poco
npm install
```

### Run eth node and deploy contracts


```
docker run -d -v ~/wallets:/root/.ethereum/net1337/keystore/ --name iexec-geth-local --entrypoint=./startupGeth.sh -p 8545:8545 iexechub/iexec-geth-local
docker logs -f iexec-geth-local
```
In an other terminal
```
cd iexec-scheduler/Poco
./node_modules/.bin/truffle test test/03_appCreation.js
```

Get
```
aRLCInstance:        0x133897cdaa5f0c26500bad4794fe49c93567eb49
aIexecHubInstance:   0x238b5faf681e3e535d92b54975589fcced1ccb0b
aAppInstance:        0x7ad48f52a0587063f3c5ac0d67f8988321c2f36b
iExecCloudUser:      0xdcdfa80f7fec63dc5a53d107b184370915d146ac
```
from logs and put values in the application.yml file
```
iexecHubAddress: ${iexecHub:0x0b21fa4fcc190380d09dec2099578d76197e7c1f}
[...]
[...]
[...]
```
Change other conf and mock values in application.yml 


### Build project
The simple gradle command:
```
gradle build
```
will copy the contracts, generate the associated java files from the contracts and compile the java code.
If you wish to skip the java generation of the contracts, just use the following command:
```
gradle build -PskipContracts
```
This can be handy when contracts don't change much to shorten the compile time.

### Run project

To start the server, the following command can be used:
```
gradle build bootRun
```
or
```
java -jar iexec-scheduler.jar --iexecHub=0x0000IEXECHUB --wallet.folder=.       [--]
```


### Mock endpoints
```
Server up?
GET http://localhost:8080/isalive

Get WorkerPool conf
GET http://localhost:8080/workerpool
```


Obsolete:
```
Create WorkOrder (on existing pool) (Synchronous call, takes ~10sec)
GET http://localhost:8080/createworkorder?app=0x000APP&beneficiary=0x000CLOUDUSER (app & beneficiary required)
(GET http://localhost:8080/createworkorder?app=0x000APP&dataset=0&workOrderParam=noTaskParam&workReward=0&askedTrust=false&dappCallback=false&beneficiary=0x000CLOUDUSER)
```



