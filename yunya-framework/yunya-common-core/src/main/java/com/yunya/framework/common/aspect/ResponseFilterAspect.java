package com.yunya.framework.common.aspect;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
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

    private final String AUDIT = "audit";

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
//        Object[] args = ret.getArgs();
//        Object o = this.reflectFieldValue(args, AUDIT, true);
//        this.setFieldValue((MethodSignature) ret.getSignature(),responseResult,AUDIT,o);


    }

    /**
     * 反射获取字段值
     * @param args   参数数组
     * @param fieldName   字段名称
     * @param reflectSupper  是否反射父类；true反射父类，false反射本类
     * @return
     */
    private Object reflectFieldValue(Object[] args,String fieldName,Boolean reflectSupper) {
        if (StringHelper.isNotEmpty(args)) {
            for (Object arg: args) {
                String s = arg.getClass().getSuperclass().toString();
                if (s.startsWith("class") && s.endsWith("BaseRequestParams")) {
                    Class<?> aClass = null;
                    if (reflectSupper) {
                        aClass = arg.getClass().getSuperclass();
                    } else {
                        aClass = arg.getClass();
                    }
                    Field[] declaredFields = aClass.getDeclaredFields();
                    for (Field field: declaredFields) {
                        if (field.getName().equalsIgnoreCase(fieldName)) {
                            try {
                                field.setAccessible(true);
                                Object o = field.get(arg);
                                return o;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 将字段值注入到返回值中
     * @param methodSignature  方法签名信息
     * @param responseResult  返回值
     * @param fieldName 字段名
     * @param value 字段值
     */
    private void setFieldValue(MethodSignature methodSignature,ResponseResult responseResult,String fieldName,Object value) {
        Class returnType = methodSignature.getReturnType();
        try {
            Field declaredField = returnType.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            try {
                declaredField.set(responseResult,value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
