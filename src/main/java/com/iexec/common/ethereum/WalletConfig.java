package com.iexec.common.ethereum;

import java.math.BigInteger;

public class WalletConfig {

    private String path;
    private String password;
    private BigInteger rlcDeposit;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigInteger getRlcDeposit() {
        return rlcDeposit;
    }

    public void setRlcDeposit(BigInteger rlcDeposit) {
        this.rlcDeposit = rlcDeposit;
    }
}
