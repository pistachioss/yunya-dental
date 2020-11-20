package com.yunya.framework.auth.runner;

import com.yunya.feign.auth.RemoteServiceAuthFeign;
import com.yunya.framework.auth.config.ServiceAuthConfig;
import com.yunya.framework.auth.config.UserAuthConfig;
import com.yunya.framework.common.exception.auth.ClientInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 客户端认证运行器（监听完成时触发）
 *
 * @author ace
 * @create 2017/11/29.
 */
@Slf4j
@Configuration
public class AuthClientRunner implements CommandLineRunner {

  /** 注入对象 */
  @Autowired private ServiceAuthConfig serviceAuthConfig;

  @Autowired private UserAuthConfig userAuthConfig;

  @Autowired private RemoteServiceAuthFeign serviceAuthFeign;

  @Override
  public void run(String... args) {
    log.info("初始化加载用户pubKey");
    try {
      refreshUserPubKey();
    } catch (Exception e) {
      log.error("初始化加载用户pubKey失败,1分钟后自动重试!", e);
    }
  }

  /** 定时刷新用户公钥信息 */
  @Scheduled(cron = "0 0/1 * * * ?")
  public void refreshUserPubKey() {
    String clientId = serviceAuthConfig.getClientId();
    String secret = serviceAuthConfig.getClientSecret();
    byte[] userPublicKey = serviceAuthFeign.getUserPublicKey(clientId, secret);
    if (userPublicKey.length <= 0) {
      throw new ClientInvalidException("Client not found or Client secret is error!");
    }
    this.userAuthConfig.setPubKeyByte(userPublicKey);
  }
}
