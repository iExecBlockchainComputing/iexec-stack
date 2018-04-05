package com.iexec.common.ethereum;

public class CommonConfiguration {

    private Web3jConfig web3jConfig;
    private WalletConfig walletConfig;
    private ContractConfig contractConfig;

    public Web3jConfig getWeb3jConfig() {
        return web3jConfig;
    }

    public void setWeb3jConfig(Web3jConfig web3jConfig) {
        this.web3jConfig = web3jConfig;
    }

    public WalletConfig getWalletConfig() {
        return walletConfig;
    }

    public void setWalletConfig(WalletConfig walletConfig) {
        this.walletConfig = walletConfig;
    }

    public ContractConfig getContractConfig() {
        return contractConfig;
    }

    public void setContractConfig(ContractConfig contractConfig) {
        this.contractConfig = contractConfig;
    }
}