package com.yunya.feign.middletable;

import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = YunyaServiceNameConstants.YUNYA_MIDDLE_TABLE)
public interface RemoteMiddleTableServiceFeign {
}
