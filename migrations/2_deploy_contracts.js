var FaucetRLC = artifacts.require("./FaucetRLC.sol");


module.exports = function(deployer, network) {
     deployer.deploy(FaucetRLC,"","","");
};