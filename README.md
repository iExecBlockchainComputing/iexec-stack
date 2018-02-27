# iExec scheduler (and worker)

### Clone project with it submodules (PoCo & rlc-token)

```
git clone --recurse-submodules https://github.com/iExecBlockchainComputing/iexec-scheduler.git
```

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

### Run

```
./node_modules/.bin/truffle test test/4_workOrderCreation.js
```

Get
```
aIexecHubInstance.address
aAppInstance.address
iExecCloudUser
```
from logs and replace in application.yml the line
```
iexecHub: ${iexecHub:0x0b21fa4fcc190380d09dec2099578d76197e7c1f}
```


Send eth to scheduler and worker
```
eth.sendTransaction({from:eth.accounts[0], to:'0x8bd535d49b095ef648cd85ea827867d358872809', value: web3.toWei(5, 'ether')})
eth.sendTransaction({from:eth.accounts[0], to:'0x70a1bebd73aef241154ea353d6c8c52d420d4f5b', value: web3.toWei(5, 'ether')})
```

Run Application.java

To start the server, the following command can be used:
```
gradle build bootRun


or

java -jar iexec-scheduler.jar --iexecHub=0x0000IEXECHUB --wallet.folder=.
```


Mock endpoints
```
Server up?
GET http://localhost:8080/isalive

Get WorkerPool address
GET http://localhost:8080/workerpool

Create WorkOrder (on existing pool) (Synchronous call, takes ~10sec)
GET http://localhost:8080/createworkorder?app=0x000APP&beneficiary=0x000CLOUDUSER (app & beneficiary required)
(GET http://localhost:8080/createworkorder?app=0x000APP&dataset=0&workOrderParam=noTaskParam&workReward=0&askedTrust=false&dappCallback=false&beneficiary=0x000CLOUDUSER)
                                   


```



