package com.qiao.common.validation.pool;

import java.util.Iterator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.InitializingBean;
import yhao.infra.apilist.ValidationForm;
import yhao.infra.web.common.validation.ValidationErrorMessage;
import yhao.infra.web.common.validation.ValidationResult;

public class ValidationPoolBean implements InitializingBean {
    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private Validator defaultValidator;
    private GenericObjectPool<Validator> pool;
    private boolean usePool;
    private int maxIdle;
    private int maxTotal;

    public ValidationPoolBean() {
        this.defaultValidator = this.factory.getValidator();
        this.usePool = true;
        this.maxIdle = 10;
        this.maxTotal = 50;
    }

    public void setUsePool(boolean usePool) {
        this.usePool = usePool;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public void afterPropertiesSet() throws Exception {
        this.pool = new GenericObjectPool(new ValidationPooledObjectFactory(), new ValidationPoolConfig(this.maxIdle, this.maxTotal));
    }

    public ValidationResult validationParam(ValidationForm param) {
        Validator validator = null;

        ValidationResult var3;
        try {
            validator = this.getValidator();
            var3 = this.checkParam(param, validator);
        } finally {
            this.close(validator);
        }

        return var3;
    }

    private ValidationResult checkParam(ValidationForm param, Validator validator) {
        ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<Object>> violations = validator.validate(param, new Class[0]);
        if (violations != null && violations.size() > 0) {
            result.setHasError(true);
            Iterator var5 = violations.iterator();

            while(var5.hasNext()) {
                ConstraintViolation violation = (ConstraintViolation)var5.next();
                ValidationErrorMessage errorMessage = new ValidationErrorMessage();
                errorMessage.setMessage(violation.getMessage());
                errorMessage.setProperty(violation.getPropertyPath().toString());
                errorMessage.setClassName(violation.getRootBeanClass().getSimpleName());
                result.addErrorMessage(errorMessage);
            }
        }

        return result;
    }

    private Validator getValidator() {
        try {
            Validator validator;
            if (this.usePool) {
                validator = (Validator)this.pool.borrowObject();
            } else {
                validator = this.defaultValidator;
            }

            return validator;
        } catch (Exception var3) {
            return null;
        }
    }

    private void close(Validator validator) {
        if (this.usePool && validator != null) {
            this.pool.returnObject(validator);
        }

    }
}
