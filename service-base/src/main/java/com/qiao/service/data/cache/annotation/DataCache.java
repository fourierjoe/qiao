package com.qiao.service.data.cache.annotation;

import java.lang.annotation.*;

/**
 * @Description:
 * @Created by ql on 2019/4/24/024 22:06
 * @Version: v1.0
 */
@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataCache {
    String value() default "";

    String[] key() default {};

    String[] condition() default {};

    int time() default 1;

    int accessThreshold() default 1;

    DataCacheTimeUnit timeUnit() default DataCacheTimeUnit.minute;
}
