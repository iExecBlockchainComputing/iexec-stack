package com.iexec.common.ethereum;

public class CommonConfiguration {

    private NodeConfig nodeConfig;
    private ContractConfig contractConfig;

    public NodeConfig getNodeConfig() {
        return nodeConfig;
    }

    public void setNodeConfig(NodeConfig nodeConfig) {
        this.nodeConfig = nodeConfig;
    }

    public ContractConfig getContractConfig() {
        return contractConfig;
    }

    public void setContractConfig(ContractConfig contractConfig) {
        this.contractConfig = contractConfig;
    }
}