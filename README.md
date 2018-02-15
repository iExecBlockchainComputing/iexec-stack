# iExec scheduler (and worker)

### Clone project with it submodules (PoCo & rlc-token)

```
git clone --recurse-submodules https://github.com/iExecBlockchainComputing/iexec-scheduler.git
```

### Build Smart contracts to .java
```
gradle copyContracts
gradle buildContracts
```

### Run

```
./node_modules/.bin/truffle test PoCo/test/4_taskRequestCreation.js
```

Get 
```
aIexecHubInstance.address
aAppInstance.address
iExecCloudUser
```
in the logs and put them in Application.java

Send eth to scheduler and worker
```
eth.sendTransaction({from:eth.accounts[0], to:'0x8bd535d49b095ef648cd85ea827867d358872809', value: web3.toWei(5, 'ether')})
eth.sendTransaction({from:eth.accounts[0], to:'0x70a1bebd73aef241154ea353d6c8c52d420d4f5b', value: web3.toWei(5, 'ether')})
```

Run Application.java
