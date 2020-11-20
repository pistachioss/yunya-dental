package com.yunya.auth.service;

import com.yunya.models.auth.Client;

import java.util.List;

/**
 * 客户端服务处理接口
 *
 * @author chow
 */
public interface AuthClientService {

  /**
   * 获取授权的客户端列表
   *
   * @param serviceId 服务ID
   * @param secret 服务密钥
   * @return
   */
  List<String> getAllowedClient(String serviceId, String secret);

  /** 客户端注册 */
  void registryClient();

  /**
   * 客户端服务校验
   *
   * @param clientId 客户端ID
   * @param secret 密钥
   * @throws Exception
   * @return Client
   */
  Client validate(String clientId, String secret) throws Exception;
}
