package com.qiao.common.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Created by ql on 2019/4/24/024 23:19
 * @Version: v1.0
 */
public class ValidationResult implements Serializable {
    private boolean hasError;
    private List<ValidationErrorMessage> errorMessages = new ArrayList();

    public ValidationResult() {
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public boolean hasError() {
        return this.hasError;
    }

    public List<ValidationErrorMessage> getErrorMessages() {
        return this.errorMessages;
    }

    public void addErrorMessage(ValidationErrorMessage errorMessage) {
        this.errorMessages.add(errorMessage);
    }
}
