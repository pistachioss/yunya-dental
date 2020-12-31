package com.yunya.feign.employee_attend;

import com.yunya.feign.employee_attend.factory.EmployeeAttendServiceFallBackFactory;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.vo.BaseEmployeeScheduleVO;
import com.yunya.feign.employee_attend.vo.EmployeeScheduleResultVO;

import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.employee_attend.EmployeeSchedule;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_EMPLOYEE_ATTEND,
        fallbackFactory = EmployeeAttendServiceFallBackFactory.class)
public interface EmployeeAttendServiceFeign {
  /**
   * 排班列表暴露接口
   *
   * @param employeeScheduleQueryForm 参数封装
   * @return
   */
  @RequestMapping(value = "/api/employee/attend/list", method = RequestMethod.POST)
  EmployeeScheduleResultVO findList(@RequestBody EmployeeScheduleQueryForm employeeScheduleQueryForm);
  /**
   * 中间表排班信息暴露接口
   *
   * @param employeeSchedule 参数封装
   * @return
   */
  @RequestMapping(value = "/api/employee/attend/findEmInfoById", method = RequestMethod.POST)
  BaseEmployeeScheduleVO findEmInfoById(@RequestBody EmployeeSchedule employeeSchedule);
}
