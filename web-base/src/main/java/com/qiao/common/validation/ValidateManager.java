package com.qiao.common.validation;

import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import yhao.infra.apilist.RequestResult;
import yhao.infra.apilist.ValidationForm;
import yhao.infra.web.bean.JsonCommonCodeEnum;
import yhao.infra.web.common.validation.pool.ValidationPoolBean;

/**
 * @Description:
 * @Created by ql on 2019/4/24/024 23:18
 * @Version: v1.0
 */
@Aspect
@Component
public class ValidateManager {
    @Autowired
    protected ValidationPoolBean validationPoolBean;
    private final String mircoServiceReturnType = RequestResult.class.getSimpleName();

    public ValidateManager() {
    }

    @Around("@annotation(rm)")
    public Object validate(ProceedingJoinPoint joinPoint, RequestMapping rm) throws Throwable {
        return this.doValidate(joinPoint);
    }

    @Around("@annotation(gm)")
    public Object validateGet(ProceedingJoinPoint joinPoint, GetMapping gm) throws Throwable {
        return this.doValidate(joinPoint);
    }

    @Around("@annotation(pm)")
    public Object validatePost(ProceedingJoinPoint joinPoint, PostMapping pm) throws Throwable {
        return this.doValidate(joinPoint);
    }

    private Object doValidate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length != 0) {
            Object[] var3 = args;
            int var4 = args.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Object arg = var3[var5];
                if (arg instanceof ValidationForm) {
                    ValidationResult result = this.validationPoolBean.validationParam((ValidationForm)arg);
                    if (result.hasError()) {
                        if (joinPoint.getSignature().toLongString().contains(this.mircoServiceReturnType)) {
                            RequestResult requestResult = new RequestResult(false, (Object)null, ((ValidationErrorMessage)result.getErrorMessages().get(0)).getMessage());
                            return requestResult;
                        }

                        Map<String, Object> jsonResult = new HashMap();
                        jsonResult.put("status", JsonCommonCodeEnum.E0002.getStatus());
                        jsonResult.put("message", ((ValidationErrorMessage)result.getErrorMessages().get(0)).getMessage());
                        jsonResult.put("result", (Object)null);
                        return JSON.toJSONString(jsonResult);
                    }
                }
            }

            return joinPoint.proceed();
        } else {
            return joinPoint.proceed();
        }
    }
}
