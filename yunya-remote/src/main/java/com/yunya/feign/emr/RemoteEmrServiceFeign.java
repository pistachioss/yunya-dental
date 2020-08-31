package com.yunya.feign.emr;

import com.yunya.framework.common.constant.*;
import org.springframework.cloud.openfeign.*;

@FeignClient(name = YunyaServiceNameConstants.YUNYA_EMR)
public interface RemoteEmrServiceFeign {
}
