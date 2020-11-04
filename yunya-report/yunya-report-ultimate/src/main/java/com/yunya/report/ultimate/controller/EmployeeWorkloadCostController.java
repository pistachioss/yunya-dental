package com.yunya.report.ultimate.controller;

import com.yunya.feign.report.domain.model.EmployeeWorkloadCostModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.EmployeeWorkloadCostBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 员工工作量消耗成本控制器
 *
 * @author: chow
 * @date: 2020/11/3 20:31
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("workload")
public class EmployeeWorkloadCostController {

  /** 员工工作量消耗成本Biz */
  @Autowired private EmployeeWorkloadCostBiz employeeWorkloadCostBiz;

  /**
   * 保存员工工作量消耗成本
   *
   * @param model 参数模型
   * @return
   */
  @CurrentUser
  @ApiOperation("保存或更新员工工作量消耗成本")
  @PostMapping(value = "/cost/update", name = "保存员工工作量消耗成本")
  public ResponseResult<T> addOrModifyCost(
      @RequestBody @Validated EmployeeWorkloadCostModel model) {
    employeeWorkloadCostBiz.addOrModifyCost(model);
    return ResponseUtil.success(null);
  }
}
