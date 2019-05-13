package com.qiao.apilist;

import java.io.Serializable;

/**
 * @Description:
 * @Created by ql on 2019/5/13/013 23:13
 * @Version: v1.0
 */
public interface JsonReturnCodeEnum extends Serializable {
    String getMessage();

    String getStatus();
}
