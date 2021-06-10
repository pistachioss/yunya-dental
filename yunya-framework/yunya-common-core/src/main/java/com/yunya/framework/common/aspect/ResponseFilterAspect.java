package com.yunya.framework.common.aspect;

import com.yunya.framework.common.model.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @program: yunya-dental
 * @description: Response返回数据拦截器
 * @author: LHB
 * @create: 2021-01-15 09:38
 **/
@Aspect
@Component
@Slf4j
public class ResponseFilterAspect {

    @Pointcut("execution(public * com.yunya..*.controller..*.*(..))")
    public void controllerExecuteAfterPointCut() {}

    @AfterReturning(value = "controllerExecuteAfterPointCut()",returning = "responseResult")
    public void doAfterReturning(JoinPoint ret, ResponseResult responseResult) {
        StringBuffer sb = new StringBuffer();
        sb.append("\n\n----------------Response返回数据拦截start-----------------\n");
        sb.append("\n==> 【controller参数】: " + Arrays.asList(ret.getArgs()));
        sb.append("\n==> 【controller路径】: " + ret.getTarget());
        sb.append("\n==> 【Response返回值】: " + responseResult.toString());
        sb.append("\n\n----------------------end-------------------------------\n\n");
        log.info("{}",sb.toString());
    }
}
