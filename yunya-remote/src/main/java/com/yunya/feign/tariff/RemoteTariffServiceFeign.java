package com.yunya.feign.tariff;

import com.yunya.feign.system.factory.RemoteSystemServiceFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 简介: 云牙价目表服务接口调用
 *
 * @author: chow
 * @date: 2020/8/6 17:50
 * @description:
 * @since: 1.0.0
 */
@FeignClient(
    name = YunyaServiceNameConstants.YUNYA_TARIFF_SERVICE,
    fallbackFactory = RemoteSystemServiceFallBackFactory.class)
public interface RemoteTariffServiceFeign {
  /***/
}
