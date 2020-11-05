package com.yunya.middletable.controller.patient;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.patient.BasePatientMemberOccurLogBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 报表中间表患者会员/预付款操作日志信息控制器
 *
 * @author: WY
 * @date: 2020/10/16 16:40
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("occurlog")
public class BasePatientMemberOccurLogController {
  /** 注入服务 */
  @Autowired private BasePatientMemberOccurLogBiz basePatientMemberOccurLogBiz;

  @PostMapping("/operate")
  public ResponseResult<T> operate(@RequestBody @Validated MessageModel model) {
    basePatientMemberOccurLogBiz.operate(model);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件拉取员工数据并更新中间表
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表员工信息")
  @PostMapping(value = "/batch", name = "PatientMemberInfoBiz")
  public ResponseResult<T> pullOccurLogData(@RequestBody PullForm form) {
    basePatientMemberOccurLogBiz.pullOccurLogData(form);
    return ResponseUtil.success(null);
  }
}
