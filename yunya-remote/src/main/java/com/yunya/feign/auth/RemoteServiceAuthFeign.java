package com.yunya.feign.auth;

import com.yunya.feign.auth.factory.RemoteServiceAuthFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 服务调用feign
 *
 * @author ace
 * @date 2017/9/15
 */
@FeignClient(
    name = YunyaServiceNameConstants.YUNYA_AUTH_SERVICE,
    fallbackFactory = RemoteServiceAuthFallBackFactory.class)
public interface RemoteServiceAuthFeign {

  /**
   * 根据客户端ID，密钥获取服务pubKey数组
   *
   * @param clientId 客户端名称
   * @param secret 客户端密钥
   * @return
   */
  @RequestMapping(value = "/client/userPubKey", method = RequestMethod.POST)
  ResponseResult getUserPublicKey(
      @RequestParam("clientId") String clientId, @RequestParam("secret") String secret);
}
