# iExec Worker

### Clone project with it submodules (PoCo & rlc-token)

```
git clone --recurse-submodules https://github.com/iExecBlockchainComputing/iexec-worker.git
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

Run scheduler

Get the WorkerPool address deployed by the Scheduler and put it in application.yml 
```
GET http://localhost:8080/workerpool

workerpool: ${workerpool:0x00000WORKERPOOLADDRESS}
```

Send eth to worker
```
eth.sendTransaction({from:eth.accounts[0], to:'0x70a1bebd73aef241154ea353d6c8c52d420d4f5b', value: web3.toWei(5, 'ether')})
```



To start the worker, the following command can be used:
```
gradle build bootRun


or

java -jar iexec-worker.jar --workerpool=0x00000WORKERPOOLADDRESS --wallet.folder=.
```


Mock endpoints
```
Worker up?
GET http://localhost:8081/isalive

```



