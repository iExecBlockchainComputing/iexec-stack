
## JUST USE IT
We have generated a local chain with 1 peer with PoCo (V2) contract already deployed. For local dev purpose.


You can use it with :
```
docker-compose up -d
```

## DIY if you want a different config
Here steps have been done to generate this config :
ubuntu prerequiste:

```
sudo su
wget https://raw.githubusercontent.com/branciard/blockchain-dev-env/master/bootstrap-ubuntu-aws.sh
chmod +x bootstrap-ubuntu-aws.sh
./bootstrap-ubuntu-aws.sh
```
git clone parity-deploy repo
```
su - ubuntu
git clone https://github.com/branciard/parity-deploy.git
cd parity-deploy
git checkout pwdgen-script-proposal
```

generate password for one node
```
cd parity-deploy
./config/utils/pwdgen.sh -n 1 -l 24
generate password in deployment/1
24
```

generate docker-compose file
```
./parity-deploy.sh --config aura --name localParityPOA --nodes 1 --ethstats --expose
```

give eth to richman

```
sed -i 's/0x00Ea169ce7e0992960D3BdE6F5D539C955316432/0x0513425AE000f5bAEaD0ed485ED8c36E737e3586/g' deployment/chain/spec.json
```
start chain
```
docker-compose up -d
docker-compose logs -f
```



```
git clone https://github.com/iExecBlockchainComputing/PoCo.git
cd PoCo
npm i
npm install truffle-hdwallet-provider-privkey
# create only one category
sed -i 's/0/4/g' migrations/2_deploy_contracts.js
#copy existing truffle.js
cp truffle.js truffle.ori
```

create a truffle.js and replace with richman private key

```
const HDWalletProvider = require("truffle-hdwallet-provider-privkey");

const privateKeys = ["ce2eab51c7c428..."]; // private keys

module.exports = {
  networks: {
    development: {
      host: "localhost",
      port: 8545,
      network_id: "*", // Match any network id
      server: 'https://localhost:443'
    },
    richman: {
      provider: () =>
        new HDWalletProvider(privateKeys, "http://localhost:8545"),
      network_id: "17",
      gas: 4710000,
      gasPrice: 22000000000,
  }
 }
,
    solc: {
        optimizer: {
            enabled: true,
            runs: 200
        }
    },
    mocha: {
        enableTimeouts: false
    }
};
```
deploy poco contract and RLC
```
./node_modules/.bin/truffle migrate --reset --network richman
```
result :
```


Using network 'richman'.

Running migration: 1_initial_migration.js
  Deploying Migrations...
  ... 0xa1fce533d6bbaac47a3dbbeae30265d4f7e134259556f02952a260dab2ce1c80
  Migrations: 0xfe33e214b55b2500bb1ae51c6b740fe1e1bffff3
Saving successful migration to network...
  ... 0x31e803d34013cc014155d038c0e177ce8202c8f2ca120a6163bc22e8d9d22457
Saving artifacts...
Running migration: 2_deploy_contracts.js
  Deploying RLC...
  ... 0x8b1bf60e73e1776e692f5a301691ea46135e02fcae0bb0a418db4111c593c55d
  RLC: 0x059118f13e5352aa70e29d24c73c22b5bb322d72
RLC deployed at address: 0x059118f13e5352aa70e29d24c73c22b5bb322d72
  ... 0xc790807c58fa77111e997e733f3d442e038fa867a2306207843addbcc03617c0
RLC unlocked
RLC faucet wallet is 0x0513425ae000f5baead0ed485ed8c36e737e3586
RLC faucet supply is 87000000000000000
  Deploying WorkerPoolHub...
  ... 0x2b826d5a37f154171d8c3de98dc970ea26f6e40496be77dfd437eadc7c9704f0
  WorkerPoolHub: 0x5822183c62a2fe7834adc65a2dd91cd6d08d5b36
WorkerPoolHub deployed at address: 0x5822183c62a2fe7834adc65a2dd91cd6d08d5b36
  Deploying AppHub...
  ... 0xb0642eaea8ff03416d075cf3153348d833e11ba696bf0b1d97fbc28df80f5081
  AppHub: 0xdaf9346b7e255c998c486fdb0968eb487129e51a
AppHub deployed at address: 0xdaf9346b7e255c998c486fdb0968eb487129e51a
  Deploying DatasetHub...
  ... 0x596bdb59af60813989f8fea0c5ba4af6c6991c01d844a694ff4d85d4bc9d82c4
  DatasetHub: 0xac7938182086d665ac8dd824dba5538771ccfab4
DatasetHub deployed at address: 0xac7938182086d665ac8dd824dba5538771ccfab4
  Deploying IexecHub...
  ... 0x160f77759b6a2679517ead13d90ee430cf2de25a1c8b0430c2c119136c69834b
  IexecHub: 0xabc654b6ba9d17e2947711849adb7ee9351d75cc
IexecHub deployed at address: 0xabc654b6ba9d17e2947711849adb7ee9351d75cc
  ... 0x83d7358ff6e162121ae53fb0a47e52804b21caa497074de44c7cd68c1228c861
setImmutableOwnership of WorkerPoolHub to IexecHub
  ... 0x18643e6c04b09813d3539b004e74349a4ff1dba0ef1ba3899e5a78c83d46ec60
setImmutableOwnership of AppHub to IexecHub
  ... 0x45bca089705d2148e54167a581922565191546d251c6de2ed3abd4f16a29486f
setImmutableOwnership of DatasetHub to IexecHub
  Deploying Marketplace...
  ... 0x149d9cf5974ef1970959ec3a26cc491d93700529ceb88ac4d4a2c2566a75a1aa
  Marketplace: 0x0f428cd91419cec5b2f2d9197e39772b41ec6d96
Marketplace deployed at address: 0x0f428cd91419cec5b2f2d9197e39772b41ec6d96
  ... 0x1eae5b2576b561d959644cf04cb420ab09956d21de6fc44ff3ad3fdfaef04f62
attach Contracts to IexecHub done
  ... 0x67baa539ee0b12003c22628358d244e866f4a43bb931d35ffb5349833d92f078
setCategoriesCreator to 0x0513425ae000f5baead0ed485ed8c36e737e3586
create category : 5
  ... 0x9d012f557d984d2e5be6ebcb0ab28a61b4db33de628e5eaa102165c730af7955
m_categoriesCount is now: 1
Saving successful migration to network...
  ... 0x37ece013694ad7515d496d89a3d6bc950ce767ff44cfd3a880dd60602d2934e0
Saving artifacts...
```


give money to 200 accounts

```
git clone https://github.com/iExecBlockchainComputing/wallets.git
cd wallets
# set the right IexecHub find in PoCo/build/contracts/IexecHub.json contract address
sed -i 's/0xc4e4a08bf4c6fd11028b714038846006e27d7be8/0xabc654b6ba9d17e2947711849adb7ee9351d75cc/g' richman/chain.json
sed -i 's/1337/17/g' richman/chain.json

./topUpWallets --from=1 --to=200 --minETH=10 --maxETH=10 --chain=dev --minRLC=1000

```
check balance :
```
curl --data '{"jsonrpc":"2.0","method":"eth_getBalance","params":["0x0513425AE000f5bAEaD0ed485ED8c36E737e3586", "latest"],"id":1}' -H "Content-Type: application/json" -X POST localhost:8545
```
