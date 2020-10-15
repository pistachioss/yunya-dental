package com.yunya.middletable.controller.organization;

import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.organization.BaseOrganizationBiz;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 报表中间表组织信息控制器
 *
 * @author: chow
 * @date: 2020/10/15 11:16
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("org")
public class BaseOrganizationController {
  @Autowired private BaseOrganizationBiz organizationBiz;

  /**
   * 根据消息操作中间表组织信息
   *
   * @param model 消息
   */
  @PostMapping("/operate")
  public ResponseResult<T> operateOrgInfo(@RequestBody @Validated MessageModel model) {
    organizationBiz.operateOrganization(model);
    return ResponseUtil.success(null);
  }


}
