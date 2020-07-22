package com.yunya.auth.rpc;

import com.yunya.auth.configuration.KeyConfiguration;
import com.yunya.auth.service.AuthClientService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 权限服务内部暴露接口
 *
 * @author: chow
 * @date: 2020/7/21 17:27
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("rpc")
public class ClientRest {

  /** 注入服务 */
  private final AuthClientService authClientService;

  private final KeyConfiguration keyConfiguration;

  public ClientRest(AuthClientService authClientService, KeyConfiguration keyConfiguration) {
    this.authClientService = authClientService;
    this.keyConfiguration = keyConfiguration;
  }

  /**
   * 获取用户公钥数组
   *
   * @param clientId 客户端编码
   * @param secret 密码
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/userPubKey", method = RequestMethod.GET)
  public byte[] getUserPublicKey(
      @RequestParam("clientId") String clientId, @RequestParam("secret") String secret)
      throws Exception {
    authClientService.validate(clientId, secret);
    return keyConfiguration.getUserPubKey();
  }
}
