package com.yunya.auth.controller;

import com.yunya.auth.form.ClientRequestForm;
import com.yunya.auth.service.AuthClientService;
import com.yunya.auth.configuration.KeyConfiguration;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简单介绍: 客户端控制器
 *
 * @author: chow
 * @date: 2020/7/5 17:03
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("client")
public class ClientController {

  /** 注入服务 */
  @Autowired private AuthClientService authClientService;

  @Autowired private KeyConfiguration keyConfiguration;

  /**
   * 通过客户端编码，密钥获取客户端列表
   *
   * @param clientRequestForm 参数封装
   * @return
   */
  @PostMapping("/list")
  public ResponseResult getAllowedClient(@RequestBody ClientRequestForm clientRequestForm) {
    List<String> clients =
        authClientService.getAllowedClient(
            clientRequestForm.getClientCode(), clientRequestForm.getSecret());
    return ResponseUtil.success(clients);
  }

  /**
   * 获取用户公钥数组
   *
   * @param clientId 客户端编码
   * @param secret 密码
   * @return
   * @throws Exception
   */
  @PostMapping("/userPubKey")
  public ResponseResult getUserPublicKey(
          @RequestParam("clientId") String clientId, @RequestParam("secret") String secret)
      throws Exception {
    authClientService.validate(clientId, secret);
    byte[] userPubKey = keyConfiguration.getUserPubKey();
    return ResponseUtil.success(userPubKey);
  }
}
