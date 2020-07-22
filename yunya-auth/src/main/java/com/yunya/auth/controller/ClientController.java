package com.yunya.auth.controller;

import com.yunya.auth.service.AuthClientService;
import com.yunya.feign.auth.form.ClientRequestForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
