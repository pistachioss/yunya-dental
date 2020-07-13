package com.yunya.framework.auth.feign;

import com.yunya.framework.common.model.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 服务权限feign
 *
 * @author ace
 * @date 2017/9/15
 */
@FeignClient("yunya-auth")
public interface ServiceAuthFeign {

  /**
   * 查询公钥字节数组
   *
   * @param clientId
   * @param secret
   * @return
   */
  @RequestMapping(value = "/client/userPubKey", method = RequestMethod.POST)
  ResponseResult getUserPublicKey(
      @RequestParam("clientId") String clientId, @RequestParam("secret") String secret);
}
