package com.yunya.feign.clinic_base;

import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessGoalCompletedInfoVO;
import com.yunya.feign.clinic_base.factory.RemoteClinicBaseServiceFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 简介: 门诊基础信息服务调用
 *
 * @author: chow
 * @date: 2021/1/18 16:59
 * @description:
 * @since: 1.0.0
 */
@FeignClient(
    name = YunyaServiceNameConstants.YUNYA_CLINIC_BASE,
    fallbackFactory = RemoteClinicBaseServiceFactory.class)
public interface RemoteClinicBaseServiceFeign {

  /**
   * 根据条件查询门诊业务目标完成情况
   *
   * @param query 查询条件
   * @return BusinessGoalCompletedInfoVO
   */
  @RequestMapping(value = "/rpc/goal/completed", method = RequestMethod.POST)
  BusinessGoalCompletedInfoVO businessGoalCompletedInfo(
      @RequestBody @Validated BusinessGoalCompletedInfoQuery query);
}
