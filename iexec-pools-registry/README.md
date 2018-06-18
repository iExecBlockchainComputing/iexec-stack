# iExec Pools Registry

In order to list your Worker Pool on the [iExec Pool Registry](https://pools.iex.ec/), you need to make it into the iExec Pools Registry. But relax, it's very easy:

Once you created and deployed your iExec WorkerPool using the [iExec SDK](https://github.com/iExecBlockchainComputing/iexec-sdk), here are the 5 steps that remain to enter this registry:

## 1. Github Fork this repo

clic on the github "Fork" button

[![github fork](./github-fork.png)](https://github.com/iExecBlockchainComputing/iexec-pools-registry/tree/v2#fork-destination-box)

## 2. Create 2 new folders

* **One** personal folder at the root **[MUST match your github user or organization name]**. Ex: `/iExecBlockchainComputing`.

* **One** folder for your pool **[MUST match your pool name]**. Ex: `/iExecBlockchainComputing/MainPool`.

## 3. Commit `iexec.json`

Check that `iexec.json` respects below format:

* **description**: Must be at least 150 letters long
* **logo.png**: Must be 180px by 180px, square shape

```json
{
  "description": "Pool description should be at least 150 letters long",
  "social": {
    "website": "ex: https://super.pool.io"
  },
  "logo": "ex: logo.png [must be 180px by 180px, square shape]",
  "workerPool": {
    "description": "iexec-main-pool",
    "subscriptionLockStakePolicy": 100,
    "subscriptionMinimumStakePolicy": 100,
    "subscriptionMinimumScorePolicy": 100
  }
}
```

## 4. Commit `deployed.json`

Check that `deployed.json` respects below format:

* **Kovan ID only => "42"**

```json
{
  "workerPool": {
    "42": "0xfd3753bc4e4c5624a5fbc41c2632bd731d7e96fb"
  }
}
```

## 5. Github Pull Request

clic on this button to create a Pull Request:

[![github pull request](./github-pr.png)](https://github.com/iExecBlockchainComputing/iexec-pools-registry/compare)

**We'll review you Pool and if it meets all the above criteria, it will be added to the iExec Pool Regsitry!**
