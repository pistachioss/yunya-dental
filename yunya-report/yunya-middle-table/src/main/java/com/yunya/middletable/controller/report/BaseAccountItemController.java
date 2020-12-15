package com.yunya.middletable.controller.report;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseAccountItemBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 中间表入账方式控制层
 *
 * @author: chow
 * @date: 2020/12/12 11:24
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("account")
public class BaseAccountItemController {

  /** 入账方式 */
  @Autowired private BaseAccountItemBiz accountItemBiz;

  /**
   * 根据消息操作中间表入账方式
   *
   * @param msg 消息
   * @return
   */
  @ApiOperation("根据消息操作中间表入账方式")
  @PostMapping(value = "/operate", name = "根据消息操作中间表入账方式")
  public ResponseResult<T> operateAccountItem(@RequestBody @Validated MessageModel msg) {
    accountItemBiz.operateAccountItem(msg);
    return ResponseUtil.success(null);
  }

  /**
   * 根据时间段批量操作中间表入账方式
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表账单收费记录")
  @PostMapping(value = "/operate/batch", name = "根据时间段批量操作中间表入账方式")
  public ResponseResult<T> pullAccountItemData(@RequestBody PullForm form) {
    accountItemBiz.pullAccountItem(form);
    return ResponseUtil.success(null);
  }
}
