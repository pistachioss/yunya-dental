package com.yunya.feign.wechat;

import com.yunya.feign.wechat.factory.RemoteWechatServiceFeignFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 简介: 云牙价目表、就诊服务接口调用
 *
 * @author: chow
 * @date: 2020/8/6 17:50
 * @description:
 * @since: 1.0.0
 */
@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_WECHAT,
        fallbackFactory = RemoteWechatServiceFeignFallBackFactory.class)
public interface RemoteWechatServiceFeign {

}
