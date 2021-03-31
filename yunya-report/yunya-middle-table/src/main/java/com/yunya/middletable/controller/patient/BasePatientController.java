package com.yunya.middletable.controller.patient;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.patient.BasePatientBiz;
import com.yunya.models.report.BasePatient;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介:报表中间表患者信息控制器
 *
 * @author: WY
 * @date: 2020/10/15 13:20
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("patient")
public class BasePatientController {
  /** 注入服务 */
  @Autowired private BasePatientBiz basePatientBiz;

  /**
   * 患者信息操作
   *
   * @param model 患者消息
   * @return ResponseResult<T>
   */
  @PostMapping("/operate")
  public ResponseResult<T> operate(@RequestBody @Validated MessageModel model) {
    basePatientBiz.operate(model);
    return ResponseUtil.success();
  }

  /**
   * 根据条件拉取患者数据并更新中间表
   *
   * @param form 拉取时间
   * @return ResponseResult<T>
   */
  @ApiOperation("根据时间段批量操作中间表员工信息")
  @PostMapping(value = "/batch", name = "PatientBaseInfoBiz")
  public ResponseResult<T> pullPatientData(@RequestBody PullForm form) throws InterruptedException {
    basePatientBiz.pullPatientData(form);
    return ResponseUtil.success();
  }

  /**
   * 修改患者信息
   *BasePatientController
   * @param basePatient 患者消息
   * @return ResponseResult<T>
   */
  @ApiOperation("修改患者信息")
  @PostMapping("/upd")
  public ResponseResult<T> upd(@RequestBody BasePatient basePatient) {
    basePatientBiz.upd(basePatient);
    return ResponseUtil.success();
  }


  @ApiOperation("修改患者信息")
  @PostMapping("/updPatientInfo")
  public ResponseResult<T> updPatientInfo() throws InterruptedException {
    basePatientBiz.updPatientInfo();
    return ResponseUtil.success();
  }
}
