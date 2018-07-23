package com.iexec.common.model;

import com.iexec.common.marketplace.MarketOrderDirectionEnum;

import java.math.BigInteger;

public class MarketOrderModel {

    //private status ?
    private BigInteger direction;
    private BigInteger category;
    private BigInteger trust;
    private BigInteger value;
    private BigInteger volume;
    private BigInteger remaining;
    private String workerpool;
    private String workerpoolOwner;

    public MarketOrderModel(BigInteger direction, BigInteger category, BigInteger trust, BigInteger value, BigInteger volume, BigInteger remaining, String workerpool, String workerpoolOwner) {
        this.direction = direction;
        this.category = category;
        this.trust = trust;
        this.value = value;
        this.volume = volume;
        this.remaining = remaining;
        this.workerpool = workerpool;
        this.workerpoolOwner = workerpoolOwner;
    }

    public BigInteger getDirection() {
        return direction;
    }

    public void setDirection(BigInteger direction) {
        this.direction = direction;
    }

    public BigInteger getCategory() {
        return category;
    }

    public void setCategory(BigInteger category) {
        this.category = category;
    }

    public BigInteger getTrust() {
        return trust;
    }

    public void setTrust(BigInteger trust) {
        this.trust = trust;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public BigInteger getVolume() {
        return volume;
    }

    public void setVolume(BigInteger volume) {
        this.volume = volume;
    }

    public BigInteger getRemaining() {
        return remaining;
    }

    public void setRemaining(BigInteger remaining) {
        this.remaining = remaining;
    }

    public String getWorkerpool() {
        return workerpool;
    }

    public void setWorkerpool(String workerpool) {
        this.workerpool = workerpool;
    }

    public String getWorkerpoolOwner() {
        return workerpoolOwner;
    }

    public void setWorkerpoolOwner(String workerpoolOwner) {
        this.workerpoolOwner = workerpoolOwner;
    }

    @Override
    public String toString() {
        return "MarketOrderModel{" +
                "direction=" + direction +
                ", category=" + category +
                ", trust=" + trust +
                ", value=" + value +
                ", volume=" + volume +
                ", remaining=" + remaining +
                ", workerpool='" + workerpool + '\'' +
                ", workerpoolOwner='" + workerpoolOwner + '\'' +
                '}';
    }
}
