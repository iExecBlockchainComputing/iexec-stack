package com.iexec.scheduler.ethereum;

import com.iexec.scheduler.workerpool.WorkerPoolConfig;

public class Configuration {

    private Web3jConfig web3jConfig;
    private ContractConfig contractConfig;
    private WalletConfig walletConfig;
    private WorkerPoolConfig workerPoolConfig;

    public Web3jConfig getWeb3jConfig() {
        return web3jConfig;
    }

    public void setWeb3jConfig(Web3jConfig web3jConfig) {
        this.web3jConfig = web3jConfig;
    }

    public ContractConfig getContractConfig() {
        return contractConfig;
    }

    public void setContractConfig(ContractConfig contractConfig) {
        this.contractConfig = contractConfig;
    }

    public WalletConfig getWalletConfig() {
        return walletConfig;
    }

    public void setWalletConfig(WalletConfig walletConfig) {
        this.walletConfig = walletConfig;
    }

    public WorkerPoolConfig getWorkerPoolConfig() {
        return workerPoolConfig;
    }

    public void setWorkerPoolConfig(WorkerPoolConfig workerPoolConfig) {
        this.workerPoolConfig = workerPoolConfig;
    }
}