package com.iexec.common.model;

import java.math.BigInteger;

public class ContributionStatusEnum {

    public final static BigInteger UNSET      = BigInteger.valueOf(0);
    public final static BigInteger AUTHORIZED = BigInteger.valueOf(1);
    public final static BigInteger CONTRIBUTED= BigInteger.valueOf(2);
    public final static BigInteger PROVED     = BigInteger.valueOf(3);
    public final static BigInteger REJECTED   = BigInteger.valueOf(4);

}
