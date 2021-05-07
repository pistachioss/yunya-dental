package com.yunya.feign.wechat;

import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya.feign.wechat.factory.RemoteWechatServiceFeignFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

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

  @ApiOperation("推送共用接口")
  @RequestMapping(value = "/wxVip/push/msg", method = RequestMethod.POST)
  void pushTemplate(@RequestBody @Validated WxTemplateMsgModel msgModel);

  @ApiOperation("批量推送共用接口")
  @PostMapping(value = "/wxVip/push/msg/batch")
  void batchPushTemplate(@RequestBody List<WxTemplateMsgModel> list);
}
