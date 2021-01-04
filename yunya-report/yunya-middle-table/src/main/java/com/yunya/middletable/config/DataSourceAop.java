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

    /**
     * report库切入点
     */
    @Pointcut("execution(* com.yunya.middletable.dao.report..*.*(..))")
    public void switchDataSourceReport() {
    }

    @Before("switchDataSourceReport()")
    public void reportBefore() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setDatabaseShardingValue("report");
    }

    @After("switchDataSourceReport()")
    public void reportAfter() {
        //清理掉当前设置的数据源，让默认的数据源不受影响
        HintManager.clear();
    }

    /**
     * discount库切入点
     */
    @Pointcut("execution(* com.yunya.middletable.dao.discount..*.*(..))")
    public void switchDataSourceDiscount() {
    }

    @Before("switchDataSourceDiscount()")
    public void discountBefore() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setDatabaseShardingValue("discount");
    }

    @After("switchDataSourceDiscount()")
    public void discountAfter() {
        //清理掉当前设置的数据源，让默认的数据源不受影响
        HintManager.clear();
    }

    /**
     * treatment库切入点
     */
    @Pointcut("execution(* com.yunya.middletable.dao.treatment..*.*(..))")
    public void switchDataSourceTreatment() {
    }

    @Before("switchDataSourceTreatment()")
    public void treatmentBefore() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setDatabaseShardingValue("treatment");
    }

    @After("switchDataSourceTreatment()")
    public void treatmentAfter() {
        //清理掉当前设置的数据源，让默认的数据源不受影响
        HintManager.clear();
    }

    /**
     * treatment_other库切入点
     */
    @Pointcut("execution(* com.yunya.middletable.dao.treatment_other..*.*(..))")
    public void switchDataSourceTreatmentOther() {
    }

    @Before("switchDataSourceTreatmentOther()")
    public void treatmentOtherBefore() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setDatabaseShardingValue("treatment-other");
    }

    @After("switchDataSourceTreatmentOther()")
    public void treatmentOtherAfter() {
        //清理掉当前设置的数据源，让默认的数据源不受影响
        HintManager.clear();
    }

    /**
     * employee_expand库切入点
     */
    @Pointcut("execution(* com.yunya.middletable.dao.employee_expand..*.*(..))")
    public void switchDataSourceEmployeeExpand() {
    }

    @Before("switchDataSourceEmployeeExpand()")
    public void empExpandBefore() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setDatabaseShardingValue("employee-expand");
    }

    @After("switchDataSourceEmployeeExpand()")
    public void empExpandAfter() {
        //清理掉当前设置的数据源，让默认的数据源不受影响
        HintManager.clear();
    }

    /**
     * employee_attend库切入点
     */
    @Pointcut("execution(* com.yunya.middletable.dao.employee_attend..*.*(..))")
    public void switchDataSourceEmployeeAttend() {
    }

    @Before("switchDataSourceEmployeeAttend()")
    public void empAttendBefore() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setDatabaseShardingValue("employee-attend");
    }

    @After("switchDataSourceEmployeeAttend()")
    public void empAttendAfter() {
        //清理掉当前设置的数据源，让默认的数据源不受影响
        HintManager.clear();
    }

    /**
     * appointment_central库切入点
     */
    @Pointcut("execution(* com.yunya.middletable.dao.appointment..*.*(..))")
    public void switchDataSourceAppointment() {
    }

    @Before("switchDataSourceAppointment()")
    public void appointmentBefore() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setDatabaseShardingValue("appointment");
    }

    @After("switchDataSourceAppointment()")
    public void appointmentAfter() {
        //清理掉当前设置的数据源，让默认的数据源不受影响
        HintManager.clear();
    }
}
