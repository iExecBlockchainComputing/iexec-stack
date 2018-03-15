package com.iexec.scheduler.sandbox;

import org.web3j.crypto.Sign;

public class Signature {

    private byte[] r;
    private byte[] s;
    private byte v;

    public Signature() {
    }

    public Signature(byte v, byte[] r, byte[] s) {
        this.r = r;
        this.s = s;
        this.v = v;
    }

    public byte[] getR() {
        return r;
    }

    public void setR(byte[] r) {
        this.r = r;
    }

    public byte[] getS() {
        return s;
    }

    public void setS(byte[] s) {
        this.s = s;
    }

    public byte getV() {
        return v;
    }

    public void setV(byte v) {
        this.v = v;
    }
}
