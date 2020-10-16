package com.yunya.middletable.controller.report;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseTariffInfoBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 根据消息操作中间表价目信息
 *
 * @author: chow
 * @date: 2020/10/16 10:03
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("tariff")
public class BaseTariffController {

  @Autowired private BaseTariffInfoBiz tariffBiz;

  /**
   * 根据消息操作中间表价目信息
   *
   * @param model 消息
   */
  @ApiOperation("根据消息操作中间表价目信息")
  @PostMapping("/operate")
  public ResponseResult<T> operateTariffInfo(@RequestBody @Validated MessageModel model) {
    tariffBiz.operateTariff(model);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件拉取价目数据并更新中间表
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表价目信息")
  @PostMapping(value = "/operate/batch", name = "EmployeeBiz")
  public ResponseResult<T> pullTariffData(@RequestBody PullForm form) {
    tariffBiz.pullTariffData(form);
    return ResponseUtil.success(null);
  }

}
