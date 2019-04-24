package com.qiao.common.validation;

import java.io.Serializable;

/**
 * @Description:
 * @Created by ql on 2019/4/24/024 23:18
 * @Version: v1.0
 */
public class ValidationErrorMessage implements Serializable {
    private String message;
    private String property;
    private String className;

    public ValidationErrorMessage() {
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
