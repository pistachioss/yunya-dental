package com.yunya.modules.employeeattend.rpc;

import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import com.yunya.modules.employeeattend.rpc.service.EmployeeScheduleSerivce;
import com.yunya.feign.employee_attend.vo.BaseEmployeeScheduleVO;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleResultVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author 杨柳絮
 * @className EmployeeAttendServiceRest
 * @description
 * @date 2020/7/23 16:00
 */
@Api("排班服务接口暴露")
@RestController
@RequestMapping("api")
public class EmployeeAttendServiceRest {

  @Autowired private EmployeeScheduleSerivce employeeScheduleSerivce;
  /**
   * 查看员工排班列表
   * @param employeeScheduleQueryForm
   * @return
   */
  @RequestMapping(value = "/employee/attend/list", method = RequestMethod.POST)
  public EmployeeScheduleResultVO findList(@RequestBody @Validated EmployeeScheduleQueryForm employeeScheduleQueryForm) {
    EmployeeScheduleResultVO employeeScheduleResultVO = employeeScheduleSerivce.findList(employeeScheduleQueryForm);
    return employeeScheduleResultVO;
  }
  /**
   * 根据排班Id查看排班信息
   * @param employeeSchedule
   * @return
   */
  @RequestMapping(value = "/employee/attend/findEmInfoById", method = RequestMethod.POST)
  public BaseEmployeeScheduleVO findEmInfoById(@RequestBody @Validated EmployeeSchedule employeeSchedule) {
    BaseEmployeeScheduleVO baseEmployeeScheduleVO = employeeScheduleSerivce.findEmInfoById(employeeSchedule.getId());
    return baseEmployeeScheduleVO;
  }
}
