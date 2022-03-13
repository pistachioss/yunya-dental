package com.yunya.modules.employeeattend.controller;

import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.employee_attend.EmployeePush;
import com.yunya.modules.employeeattend.biz.EmployeePushBiz;
import com.yunya.modules.employeeattend.form.EmployeePushForm;
import com.yunya.modules.employeeattend.util.JpushManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Set;

@Api(tags = "推送接口")
@RestController
@RequestMapping("/employee_push")
@CrossOrigin
public class EmployeePushController {
  @Autowired private EmployeePushBiz employeePushBiz;

  /**
   * 保存员工用户推送关系
   *
   * @param employeePush
   */
  @PostMapping("/save")
  @ApiOperation("设备添加，绑定到员工，新增或编辑")
  @RepeatSubmit
  public ResponseResult save(@RequestBody EmployeePush employeePush) throws ParseException {
    return ResponseUtil.success(employeePushBiz.save(employeePush));
  }

  /**
   * 保存员工用户推送关系
   *
   * @param employee_ids
   */
  @PostMapping("/query")
  @ApiOperation("查询绑定员工")
  @RepeatSubmit
  public ResponseResult query(@RequestBody Set<Integer> employee_ids) throws ParseException {
    return ResponseUtil.success(employeePushBiz.query(employee_ids));
  }

  /**
   * 测试推送消息
   *
   * @param employeePushTest
   * @return
   * @throws ParseException
   */
  @Deprecated
  @PostMapping("/test")
  @ApiOperation("测试推送消息")
  @RepeatSubmit
  public ResponseResult test(@RequestBody EmployeePushForm employeePushTest) throws ParseException {
    JpushManager.getInstance().pushTest(employeePushTest);
    return ResponseUtil.success();
  }
}
