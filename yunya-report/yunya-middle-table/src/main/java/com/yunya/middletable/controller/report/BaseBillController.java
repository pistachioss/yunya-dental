package com.yunya.middletable.controller.report;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseBillBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

/**
 * 简介: 中间表账单同步控制器
 *
 * @author: chow
 * @date: 2020/10/21 16:50
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("bill")
public class BaseBillController {

  @Autowired private BaseBillBiz billBiz;

  /**
   * 根据消息操作中间表账单
   *
   * @param msg 消息
   * @return
   */
  @ApiOperation("根据消息操作中间表账单")
  @PostMapping(value = "/operate", name = "根据消息操作中间表账单")
  public ResponseResult<T> operateBill(@RequestBody @Validated MessageModel msg) {
    billBiz.operateBill(msg);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件拉取账单数据并更新中间表
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表账单")
  @PostMapping(value = "/operate/batch", name = "form")
  public ResponseResult<T> pullBillData(@RequestBody PullForm form) throws ParseException {
    billBiz.pullBillData(form);
    return ResponseUtil.success(null);
  }
}
