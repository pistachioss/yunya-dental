package com.yunya.feign.oss.factory;

import com.yunya.feign.oss.RemoteOssServiceFeign;
import com.yunya.feign.oss.domain.model.Base64UploadForm;
import com.yunya.feign.oss.domain.model.OssUrlForm;
import com.yunya.framework.common.model.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RemoteOssServiceFeignFallBackFactory implements RemoteOssServiceFeign {


    @Override
    public ResponseResult getUrl(List<OssUrlForm> ossUrlForms) throws Exception {
        return null;
    }

    @Override
    public ResponseResult uploadBase64Image(Base64UploadForm ossUploadForm) {
        return null;
    }

    @Override
    public ResponseResult<Map<String, String>> getUrlMap(List<OssUrlForm> ossUrlForms) {
        return null;
    }

}
