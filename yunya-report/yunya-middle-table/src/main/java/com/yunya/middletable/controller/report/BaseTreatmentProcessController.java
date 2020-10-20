package com.yunya.middletable.controller.report;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseTreatmentProcessBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 中间表就诊流程控制器
 *
 * @author: chow
 * @date: 2020/10/17 12:49
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("treatment_process")
public class BaseTreatmentProcessController {

  @Autowired private BaseTreatmentProcessBiz treatmentProcessBiz;

  /**
   * 根据消息操作中间表就诊流程
   *
   * @param msg 消息
   * @return
   */
  @ApiOperation("根据消息操作中间表就诊流程")
  @PostMapping(value = "/operate", name = "根据消息操作中间表就诊流程")
  public ResponseResult<T> operateTreatmentProcess(@RequestBody @Validated MessageModel msg) {
    treatmentProcessBiz.operateTreatmentProcess(msg);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件拉取就诊流程数据并更新中间表
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表就诊流程")
  @PostMapping(value = "/operate/batch", name = "form")
  public ResponseResult<T> pullTreatmentProcessData(@RequestBody PullForm form) {
    treatmentProcessBiz.pullTreatmentProcessData(form);
    return ResponseUtil.success(null);
  }
}
