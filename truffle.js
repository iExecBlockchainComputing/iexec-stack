module.exports = {
  networks: {
    development: {
      host: "localhost",
      port: 8545,
      network_id: "*", // Match any network id
      server: 'https://localhost:443'
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
     rsk: {
 	    host: "IP_RSK_TESTNET",
      port: 4444,
      network_id: "31", // Match any network id
      gasPrice: 20000,
      gas: 250000,
      from: "A_PUBLIC_KEY",
     }
  }
};
/*

*/
