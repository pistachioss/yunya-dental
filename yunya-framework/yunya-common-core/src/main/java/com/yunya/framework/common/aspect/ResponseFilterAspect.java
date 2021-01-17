package com.yunya.framework.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

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

    @After("controllerExecuteAfterPointCut()")
    public void doAfterReturning(JoinPoint ret){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        log.info("----------------Response返回数据拦截start-----------------");
        log.info("==> 【controller参数】: {}",ret.getArgs());
        log.info("==> 【controller路径】: {}",ret.getTarget());
        log.info("==> 【Response返回状态】: {}",response.getStatus());
        log.info("----------------------end-------------------------------");
    }
}
