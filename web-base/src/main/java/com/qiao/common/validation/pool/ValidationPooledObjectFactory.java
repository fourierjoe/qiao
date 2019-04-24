package com.qiao.common.validation.pool;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class ValidationPooledObjectFactory extends BasePooledObjectFactory<Validator> {
    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    public ValidationPooledObjectFactory() {
    }

    public Validator create() throws Exception {
        return factory.getValidator();
    }

    public PooledObject wrap(Validator obj) {
        return new DefaultPooledObject(obj);
    }
}
