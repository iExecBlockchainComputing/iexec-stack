# iExec scheduler (and worker)


### Smart contracts to .java
```
solc IexecHub.sol --bin --abi --optimize -o .
web3j solidity generate abi/IexecHub.bin abi/IexecHub.abi -o java -p com.iexec.scheduler
```

### Run

```
git clone https://github.com/iExecBlockchainComputing/PoCo/
./node_modules/.bin/truffle test test/4_taskRequestCreation.js
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
