package com.yunya.modules.employeeattend.controller;


import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.modules.employeeattend.form.EmployeeScheduleCopyForm;
import com.yunya.modules.employeeattend.form.EmployeeScheduleDeleteForm;
import com.yunya.modules.employeeattend.form.EmployeeScheduleForm;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import com.yunya.modules.employeeattend.service.EmployeeScheduleBiz;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleController
 * @description
 * @date 2020/7/9 10:27
 */
@Api(tags = "排班接口")
@RestController
@RequestMapping("/employee_schedule")
@CrossOrigin
public class EmployeeScheduleController {
  @Autowired
  private EmployeeScheduleBiz employeeScheduleBiz;

  /**
   * 删除排班表
   *
   * @param employeeScheduleDeleteForm
   */
  @DeleteMapping
  public ResponseResult delete(@RequestBody @Validated EmployeeScheduleDeleteForm employeeScheduleDeleteForm) {
    EmployeeSchedule employeeSchedule = EntityUtils.build(employeeScheduleDeleteForm, EmployeeSchedule.class);
    employeeScheduleBiz.delete(employeeSchedule);
    return ResponseUtil.success();
  }

  /**
   * 查看员工排班列表
   * @param employeeScheduleQueryForm
   * @return
   */
  @PostMapping("/findList")
  public ResponseResult findList(@RequestBody @Validated EmployeeScheduleQueryForm employeeScheduleQueryForm) {
    return ResponseUtil.success(employeeScheduleBiz.findList(employeeScheduleQueryForm));
  }

  /**
   * 复制排班表
   *
   * @param employeeScheduleCopyForm
   * @return
   */
  @PostMapping("/copy")
  public ResponseResult copy(@RequestBody @Validated EmployeeScheduleCopyForm employeeScheduleCopyForm) {
    return ResponseUtil.success(employeeScheduleBiz.copy(employeeScheduleCopyForm));
  }

  /**
   * 添加
   *
   * @param employeeScheduleForm
   */
  @PostMapping("/create")
  public ResponseResult create(@RequestBody EmployeeScheduleForm employeeScheduleForm) throws ParseException {
    employeeScheduleBiz.create(employeeScheduleForm);
    return ResponseUtil.success();
  }


  /**
   * 导出
   *
   * @param
   * @throws Exception
   */
  @GetMapping("/export")
  public void export(
          HttpServletResponse response, EmployeeScheduleQueryForm employeeScheduleQueryForm
  ) throws Exception {
    employeeScheduleBiz.export(response, employeeScheduleQueryForm);
  }

}
