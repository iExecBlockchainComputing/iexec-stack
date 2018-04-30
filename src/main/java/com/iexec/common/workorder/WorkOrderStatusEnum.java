package com.iexec.common.marketplace;

import java.math.BigInteger;

public class WorkOrderStatusEnum {

    public final static BigInteger UNSET       = BigInteger.valueOf(0);
    public final static BigInteger ACTIVE      = BigInteger.valueOf(1);
    public final static BigInteger REVEALING   = BigInteger.valueOf(2);
    public final static BigInteger CLAIMED     = BigInteger.valueOf(3);
    public final static BigInteger COMPLETED   = BigInteger.valueOf(4);

}
