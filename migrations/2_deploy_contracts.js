var FaucetRLC = artifacts.require("./FaucetRLC.sol");


module.exports = function(deployer, network) {
     deployer.deploy(FaucetRLC, "0x1d78323c836d6e6681fe77128ae55923c8d5e0f0",
                                "0xbaa3fdc9a5f3bdd49d5dff7d29c41839ba9764e4",
                                "0x4cd3783ac37ff59a88ad06a96c68f5311dbb9b20",
                                {gas: 1916452});
};
