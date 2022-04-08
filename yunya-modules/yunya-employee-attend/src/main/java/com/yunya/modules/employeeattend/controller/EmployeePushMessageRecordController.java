package com.yunya.modules.employeeattend.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.form.EmployeePushMessageRecordForm;
import com.yunya.feign.employee_attend.form.EmployeePushMessageRecordQueryForm;
import com.yunya.feign.employee_attend.vo.EmployeePushMessageRecordVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.EmployeePushMessageRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "推送消息记录控制器")
@RestController
@RequestMapping("/empPushMsg")
@CrossOrigin
public class EmployeePushMessageRecordController {
  @Autowired private EmployeePushMessageRecordBiz employeePushMessageRecordBiz;

  /**
   * 根据条件查询列表
   *
   * @param query
   */
  @PostMapping("/list")
  @ApiOperation("根据条件查询列表")
  @CurrentUser
  public ResponseResult<PageInfo<EmployeePushMessageRecordVO>> findList(@RequestBody @Validated EmployeePushMessageRecordQueryForm query) {
    return ResponseUtil.success(employeePushMessageRecordBiz.findList(query));
  }

  /**
   * 推送消息状态更新已读
   *
   * @param form
   */
  @PutMapping("/uptHaveRead")
  @ApiOperation("推送消息状态更新已读")
  @CurrentUser
  public ResponseResult<PageInfo<EmployeePushMessageRecordVO>> uptPushMessageHaveRead(@RequestBody @Validated EmployeePushMessageRecordForm form) {
    employeePushMessageRecordBiz.uptPushMessageHaveRead(form);
    return ResponseUtil.success();
  }
}
