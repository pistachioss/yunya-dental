package com.yunya.feign.treatment;

import com.yunya.feign.treatment.factory.RemoteTreatmentServiceFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 简介: 云牙诊疗服务接口调用Feign
 *
 * @author: chow
 * @date: 2020/8/14 20:52
 * @description:
 * @since: 1.0.0
 */
@FeignClient(
    value = YunyaServiceNameConstants.YUNYA_TREATMENT_SERVICE,
    fallback = RemoteTreatmentServiceFallBackFactory.class)
public interface RemoteTreatmentServiceFeign {
  /***/
}
