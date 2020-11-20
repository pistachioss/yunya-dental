package com.yunya.auth.service.impl;

import com.yunya.models.auth.Client;
import com.yunya.auth.mapper.ClientMapper;
import com.yunya.auth.service.AuthClientService;
import com.yunya.framework.common.exception.auth.ClientInvalidException;
import com.yunya.framework.common.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端权限接口实现
 *
 * @author ace
 * @date 2017/9/10
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DBAuthClientService implements AuthClientService {

  /** 注入对象 */
  @Autowired private ClientMapper clientMapper;
  /** 服务发现客户端 */
  @Autowired private DiscoveryClient discovery;

  private ApplicationContext context;

  @Autowired
  public DBAuthClientService(ApplicationContext context) {
    this.context = context;
  }

  /**
   * 客户端信息校验
   *
   * @param clientId 客户端ID
   * @param secret 密钥
   */
  @Override
  public Client validate(String clientId, String secret) {
    Client client = new Client();
    client.setCode(clientId);
    client.setSecret(secret);
    return clientMapper.selectOne(client);
  }

  /**
   * 根据服务ID获取客户端的编码
   *
   * @param clientCode 客户端编码
   * @param secret 服务密钥
   * @return
   */
  @Override
  public List<String> getAllowedClient(String clientCode, String secret) {
    Client info = this.getClient(clientCode, secret);
    List<String> clients = clientMapper.selectAllowedClient(info.getId().toString());
    if (clients == null) {
      clients = new ArrayList<>();
    }
    return clients;
  }

  /**
   * 根据客户端编码、密钥获取客户端信息
   *
   * @param clientCode 客户端编码
   * @param secret 客户端密钥
   * @return Client
   */
  private Client getClient(String clientCode, String secret) {
    Client client = new Client();
    client.setCode(clientCode);
    client = clientMapper.selectOne(client);
    if (client == null || !client.getSecret().equals(secret)) {
      throw new ClientInvalidException("Client not found or Client secret is error!");
    }
    return client;
  }

  /** 服务自动注册 */
  @Override
  @Scheduled(cron = "0 0/1 * * * ?")
  public void registryClient() {
    // 自动注册节点
    discovery
        .getServices()
        .forEach(
            (name) -> {
              Client client = new Client();
              client.setName(name);
              client.setCode(name);
              Client dbClient = clientMapper.selectOne(client);
              if (dbClient == null) {
                client.setSecret(UUIDUtils.generateShortUuid());
                clientMapper.insert(client);
              }
            });
  }
}
