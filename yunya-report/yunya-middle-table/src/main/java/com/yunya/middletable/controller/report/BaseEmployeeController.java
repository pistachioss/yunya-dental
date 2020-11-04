package com.yunya.middletable.controller.report;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseEmployeeBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 报表员工信息中间表控制器
 *
 * @author: chow
 * @date: 2020/10/15 16:35
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("employee")
public class BaseEmployeeController {

  @Autowired private BaseEmployeeBiz employeeBiz;

  /**
   * 根据消息操作中间表员工信息
   *
   * @param model 消息
   */
  @ApiOperation("根据消息操作中间表员工信息")
  @PostMapping("/operate")
  public ResponseResult<T> operateEmpInfo(@RequestBody @Validated MessageModel model) {
    employeeBiz.operateEmployee(model);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件拉取员工数据并更新中间表
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表员工信息")
  @PostMapping(value = "/operate/batch", name = "form")
  public ResponseResult<T> pullEmpData(@RequestBody PullForm form) {
    employeeBiz.pullEmpData(form);
    return ResponseUtil.success(null);
  }
}
