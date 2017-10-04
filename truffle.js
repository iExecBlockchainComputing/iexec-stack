module.exports = {
  networks: {
    development: {
      host: "localhost",
      port: 8545,
      network_id: "*", // Match any network id
    },
    ropsten: {
      host: "localhost",
      port: 8543,
      network_id: "3",
      from:"",
      // gasPrice: 21000000000
      // gas: 400000
    },
    rinkeby: {
      host: "localhost",
      port: 8544,
      network_id: "4",
      from:"",
    },
    kovan: {
      host: "localhost",
      port: 8542,
      network_id: "42",
      from:"",
     },
  }
};
