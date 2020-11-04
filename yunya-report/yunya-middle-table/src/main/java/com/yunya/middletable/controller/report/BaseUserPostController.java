package com.yunya.middletable.controller.report;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseUserPostBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 中间表用户可登录用户可登录组织控制层
 *
 * @author: chow
 * @date: 2020/10/16 19:16
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("user_post")
public class BaseUserPostController {

  @Autowired private BaseUserPostBiz userPostBiz;

  /**
   * 根据消息操作中间表用户可登录组织信息
   *
   * @param msg 消息
   */
  @ApiOperation("根据消息操作中间表用户可登录组织信息")
  @PostMapping("/operate")
  public ResponseResult<T> operateUserPostInfo(@RequestBody @Validated MessageModel msg) {
    userPostBiz.operateUserPost(msg);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件拉取用户可登录组织数据并更新中间表
   *
   * @param form 拉取时间
   * @return
   */
  @ApiOperation("根据时间段批量操作中间表用户可登录组织信息")
  @PostMapping(value = "/operate/batch", name = "form")
  public ResponseResult<T> pullUserPostData(@RequestBody PullForm form) {
    userPostBiz.pullUserPostData(form);
    return ResponseUtil.success(null);
  }
}
