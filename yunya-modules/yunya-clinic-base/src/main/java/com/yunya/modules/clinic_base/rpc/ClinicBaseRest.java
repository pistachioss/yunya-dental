package com.yunya.modules.clinic_base.rpc;

import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessGoalCompletedInfoVO;
import com.yunya.modules.clinic_base.biz.BusinessTargetBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 门诊基础数据接口暴露
 *
 * @author: chow
 * @date: 2021/1/18 16:51
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("rpc")
public class ClinicBaseRest {

  @Autowired private BusinessTargetBiz businessTargetBiz;

  /**
   * 根据条件查询门诊业务目标完成情况
   *
   * @param query 查询条件
   * @return
   */
  @RequestMapping(value = "/goal/completed", method = RequestMethod.POST)
  public BusinessGoalCompletedInfoVO businessGoalCompletedInfo(
      @RequestBody @Validated BusinessGoalCompletedInfoQuery query) {
    return businessTargetBiz.findBusinessGoalCompletedInfo(query);
  }
}
