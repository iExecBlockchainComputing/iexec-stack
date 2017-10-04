var RLC = artifacts.require("./FaucetRLC.sol");


const Promise = require("bluebird");
//extensions.js : credit to : https://github.com/coldice/dbh-b9lab-hackathon/blob/development/truffle/utils/extensions.js
const Extensions = require("../utils/extensions.js");
const addEvmFunctions = require("../utils/evmFunctions.js");
addEvmFunctions(web3);
Promise.promisifyAll(web3.eth, {
  suffix: "Promise"
});
Promise.promisifyAll(web3.version, {
  suffix: "Promise"
});
Promise.promisifyAll(web3.evm, {
  suffix: "Promise"
});
Extensions.init(web3, assert);


contract('FaucetRLC', function(accounts) {

  var rlcCreator, faucetAgent1, faucetAgent2, faucetAgent3;
  var amountGazProvided = 4000000;
  let isTestRPC;
  let testTimemout = 0;
  let aRLCInstance;

  before("should prepare accounts and check TestRPC Mode", function() {
    assert.isAtLeast(accounts.length, 4, "should have at least 4 accounts");
    rlcCreator = accounts[0];
    faucetAgent1 = accounts[1];
    faucetAgent2 = accounts[2];
    faucetAgent3 = accounts[3];
    return Extensions.makeSureAreUnlocked(
        [rlcCreator, faucetAgent1, faucetAgent2, faucetAgent3])
      .then(() => web3.eth.getBalancePromise(rlcCreator))
      .then(balance => assert.isTrue(
        web3.toWei(web3.toBigNumber(35), "ether").lessThan(balance),
        "rlcCreator should have at least 35 ether, not " + web3.fromWei(balance, "ether")))
      .then(() => Extensions.refillAccount(rlcCreator, faucetAgent1, 10))
      .then(() => Extensions.refillAccount(rlcCreator, faucetAgent2, 10))
      .then(() => Extensions.refillAccount(rlcCreator, faucetAgent3, 10))
      .then(() => web3.version.getNodePromise())
      .then(node => isTestRPC = node.indexOf("EthereumJS TestRPC") >= 0)
      .then(() => {
        return RLC.new(faucetAgent1, faucetAgent2, faucetAgent3, {
          from: rlcCreator,
          gas: amountGazProvided
        });
      })
      .then(instance => {
        aRLCInstance = instance;
      });
  });

  it("Test intial balance of rlcCreator", function() {

    return Promise.all([
        aRLCInstance.balanceOf.call(rlcCreator),
        aRLCInstance.balanceOf.call(faucetAgent1),
        aRLCInstance.balanceOf.call(faucetAgent2),
        aRLCInstance.balanceOf.call(faucetAgent3)
      ])
      .then(balances => {
        assert.strictEqual(balances[0].toNumber(), 0, "rlcCreator");
        assert.strictEqual(balances[1].toNumber(), 29000000000000000, "faucetAgent1");
        assert.strictEqual(balances[2].toNumber(), 29000000000000000, "faucetAgent2");
        assert.strictEqual(balances[3].toNumber(), 29000000000000000, "faucetAgent3");
      })
  });



});
