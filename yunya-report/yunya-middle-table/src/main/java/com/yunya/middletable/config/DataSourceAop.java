package com.yunya.middletable.config;

import org.apache.shardingsphere.api.hint.HintManager;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 数据源切换配置，通过dao的不同配置对应的数据源
 */
@Aspect
@Order(1)
@Component
public class DataSourceAop {

    /**
     * system库切入点
     */
    @Pointcut("execution(* com.yunya.middletable.dao.system..*.*(..))")
    public void switchDataSourceDb01() {
    }

    @Before("switchDataSourceDb01()")
    public void doDb01Before() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setDatabaseShardingValue("system");
    }

    @After("switchDataSourceDb01()")
    public void doDb01after() {
        //清理掉当前设置的数据源，让默认的数据源不受影响
        HintManager.clear();
    }


    /**
     * patient库切入点
     */
    @Pointcut("execution(* com.yunya.middletable.dao.patient..*.*(..))")
    public void switchDataSourceDb02() {
    }

    @Before("switchDataSourceDb02()")
    public void doDb02Before() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setDatabaseShardingValue("patient");
    }

    @After("switchDataSourceDb02()")
    public void doDb02after() {
        //清理掉当前设置的数据源，让默认的数据源不受影响
        HintManager.clear();
    }
}
