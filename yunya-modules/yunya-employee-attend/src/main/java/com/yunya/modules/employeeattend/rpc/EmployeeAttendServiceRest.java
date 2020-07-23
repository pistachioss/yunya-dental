package com.yunya.modules.employeeattend.rpc;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.EmployeeScheduleBiz;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import com.yunya.modules.employeeattend.rpc.service.EmployeeScheduleSerivce;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
  public Map<String, Object> findList(@RequestBody @Validated EmployeeScheduleQueryForm employeeScheduleQueryForm) {
    return employeeScheduleSerivce.findList(employeeScheduleQueryForm);
  }
}
