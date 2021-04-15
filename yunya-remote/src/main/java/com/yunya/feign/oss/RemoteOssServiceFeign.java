package com.yunya.feign.oss;

import com.yunya.feign.oss.domain.model.*;
import com.yunya.feign.oss.factory.*;
import com.yunya.framework.common.constant.*;
import com.yunya.framework.common.model.*;
import io.swagger.annotations.*;
import org.springframework.cloud.openfeign.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_ALIYUN_OSS,
        fallbackFactory = RemoteOssServiceFeignFallBackFactory.class)
public interface RemoteOssServiceFeign {
    @RequestMapping(value = "url/multi", method = RequestMethod.POST)
    @ApiOperation("2.多资源：获取外网访问URL列表")
    public ResponseResult getUrl(@RequestBody List<OssUrlForm> ossUrlForms) throws Exception;
}
