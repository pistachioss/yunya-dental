package com.yunya.feign.oss.factory;

import com.yunya.feign.oss.*;
import com.yunya.feign.oss.domain.model.*;
import com.yunya.framework.common.model.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Slf4j
@Component
public class RemoteOssServiceFeignFallBackFactory implements RemoteOssServiceFeign {


    @Override
    public ResponseResult getUrl(List<OssUrlForm> ossUrlForms) throws Exception {
        return null;
    }
}
