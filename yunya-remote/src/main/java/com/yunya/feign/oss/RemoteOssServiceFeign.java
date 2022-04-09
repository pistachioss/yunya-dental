package com.yunya.feign.oss;

import com.yunya.feign.oss.domain.model.Base64UploadForm;
import com.yunya.feign.oss.domain.model.OssUrlForm;
import com.yunya.feign.oss.factory.RemoteOssServiceFeignFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_ALIYUN_OSS,
        fallbackFactory = RemoteOssServiceFeignFallBackFactory.class)
public interface RemoteOssServiceFeign {
    @RequestMapping(value = "url/multi", method = RequestMethod.POST)
    @ApiOperation("2.多资源：获取外网访问URL列表")
    public ResponseResult getUrl(@RequestBody List<OssUrlForm> ossUrlForms) throws Exception;

    @RequestMapping(value = "uploadBase64Image", method = RequestMethod.POST)
    @ApiOperation("1.base64图片上传")
    ResponseResult uploadBase64Image(@RequestBody Base64UploadForm ossUploadForm);

    @RequestMapping(value = "url/map", method = RequestMethod.POST)
    @ApiOperation("2.多资源：获取外网访问URL列表")
    ResponseResult<Map<String, String>> getUrlMap(@RequestBody List<OssUrlForm> ossUrlForms);
}
