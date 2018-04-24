package com.iexec.common.model;

import java.math.BigInteger;

public class DatasetModel {

    private String owner;
    private String name;
    private BigInteger price;
    private String params;

    public DatasetModel(String owner, String name, BigInteger price, String params) {
        this.owner = owner;
        this.name = name;
        this.price = price;
        this.params = params;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "DatasetModel{" +
                "owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", params='" + params + '\'' +
                '}';
    }
}
