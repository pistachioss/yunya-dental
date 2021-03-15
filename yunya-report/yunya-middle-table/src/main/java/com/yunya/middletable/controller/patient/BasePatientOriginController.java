package com.yunya.middletable.controller.patient;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.patient.BasePatientOriginBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 简介：患者来源中间表同步
 *
 * @author: WY
 * @date: 2020/10/15 13:20
 * @description: 患者来源中间表同步-控制层
 * @since: 1.0.0
 */

@RestController
@RequestMapping("patientOrigin")
public class BasePatientOriginController {
  /** 注入服务 */
  @Resource
  private BasePatientOriginBiz basePatientOriginBiz;

  /**
   * 操作中间表患者来源
   * @param model 条件模型
   */
  @PostMapping("/operate")
  public ResponseResult<T> operate(@RequestBody @Validated MessageModel model) {
    basePatientOriginBiz.operate(model);
    return ResponseUtil.success();
  }

  /**
   * 根据条件拉取患者数据并更新中间表
   * @param form 拉取时间
   */
  @ApiOperation("批量同步患者来源")
  @PostMapping(value = "/batch", name = "PatientBaseInfoBiz")
  public ResponseResult<T> pullPatientData(@RequestBody PullForm form) throws InterruptedException {
    basePatientOriginBiz.pullPatientData(form);
    return ResponseUtil.success();
  }


}
