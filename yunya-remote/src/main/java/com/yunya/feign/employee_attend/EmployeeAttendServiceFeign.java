package com.yunya.feign.employee_attend;

import com.yunya.feign.employee_attend.factory.EmployeeAttendServiceFallBackFactory;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.system.form.JwtRequestFrom;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_EMPLOYEE_ATTEND,
        fallbackFactory = EmployeeAttendServiceFallBackFactory.class)
public interface EmployeeAttendServiceFeign {
  /**
   * 校验用户合法性
   *
   * @param employeeScheduleQueryForm 参数封装
   * @return
   */
  @RequestMapping(value = "/api/employee/attend/list", method = RequestMethod.POST)
  Map<String, Object> findList(@RequestBody EmployeeScheduleQueryForm employeeScheduleQueryForm);
}
