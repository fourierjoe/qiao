package com.qiao.common.validation.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class ValidationPoolConfig extends GenericObjectPoolConfig {
    public ValidationPoolConfig(int maxIdle, int maxTotal) {
        this.setMaxIdle(maxIdle);
        this.setMaxTotal(maxTotal);
    }
}
