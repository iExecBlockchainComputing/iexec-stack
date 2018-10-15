# iExec Pools Registry

In order to list your Worker Pool on the [iExec Pool Registry](https://pools.iex.ec/), you need to make it into the iExec Pools Registry. But relax, it's very easy:

Once you created and deployed your iExec WorkerPool using the [iExec SDK](https://github.com/iExecBlockchainComputing/iexec-sdk), here are the 5 steps that remain to enter this registry:

## 1. Github Fork this repo

clic on the github "Fork" button and `git clone` the **forked** repository on your local machine.

[![github fork](./github-fork.png)](https://github.com/iExecBlockchainComputing/iexec-pools-registry/tree/v2#fork-destination-box)

## 2. Create 2 new folders

- **One** organization folder at the root **[MUST match your github user or github org name]**. Ex: `/iExecBlockchainComputing`.

- **One** folder for your pool inside your org folder **[MUST match your pool name]**. Ex: `/iExecBlockchainComputing/MainPool`.

## 3. Validate

Enter your pool folder, and run the below command to check its config:

- `iexec registry validate workerpool`

## 4. Commit

Once the validation is successful, you can commit & push your pool config.

- `git add iexec.json deployed.json logo.png README.md`
- `git commit -m 'adding my MainPool workerpool'`
- `git push`

## 5. Github Pull Request

clic on this button to create a Pull Request (from your **forked master branch** TO **iexec-pools-registry master branch**):

[![github pull request](./github-pr.png)](https://github.com/iExecBlockchainComputing/iexec-pools-registry/compare)

**We'll review you Pool and if it meets all the above criteria, it will be added to the iExec Pool Regsitry!**
