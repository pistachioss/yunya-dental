package com.yunya.feign.employee_attend.factory;

import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;

import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 简介: 排班服务调用降级处理
 *
 * @author: chow
 * @date: 2020/7/9 12:25
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Component
public class EmployeeAttendServiceFallBackFactory implements EmployeeAttendServiceFeign {


  @Override
  public Map<String, Object> findList(EmployeeScheduleQueryForm employeeScheduleQueryForm) {
    return null;
  }
}
