package com.yunya.auth.runner;

import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.auth.configuration.KeyConfiguration;
import com.yunya.framework.common.utils.jwt.RsaKeyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 鉴权服务运行器
 *
 * <p>【鉴权服务启动时初始化KeyConfiguration， 运行结束后会调用该运行器， 设置用户鉴权的pubKey/priKey， pri用来加密token, pub用来解密token】
 *
 * @author ace
 * @create 2017/12/17.
 */
@Configuration
public class AuthServerRunner implements CommandLineRunner {

  /** 注入对象 */
  @Autowired private RedisUtils redisUtils;

  @Autowired private KeyConfiguration keyConfiguration;

  private static final String REDIS_USER_PRI_KEY = "CLOUD_V1:AUTH:JWT:PRI";

  private static final String REDIS_USER_PUB_KEY = "CLOUD_V1:AUTH:JWT:PUB";

  /**
   * 缓存用户鉴权公钥、密钥
   *
   * @param args 参数列表
   * @throws Exception
   */
  @Override
  public void run(String... args) throws Exception {
    if (redisUtils.hasKey(REDIS_USER_PRI_KEY) && redisUtils.hasKey(REDIS_USER_PUB_KEY)) {
      // 从缓存中取出用户鉴权私钥
      keyConfiguration.setUserPriKey(RsaKeyHelper.toBytes(redisUtils.get(REDIS_USER_PRI_KEY)));
      // 从缓存中取出用户鉴权公钥
      keyConfiguration.setUserPubKey(RsaKeyHelper.toBytes(redisUtils.get(REDIS_USER_PUB_KEY)));
    } else {
      Map<String, byte[]> keyMap = RsaKeyHelper.generateKey(keyConfiguration.getUserSecret());
      keyConfiguration.setUserPriKey(keyMap.get("pri"));
      keyConfiguration.setUserPubKey(keyMap.get("pub"));
      redisUtils.set(REDIS_USER_PRI_KEY, RsaKeyHelper.toHexString(keyMap.get("pri")));
      redisUtils.set(REDIS_USER_PUB_KEY, RsaKeyHelper.toHexString(keyMap.get("pub")));
    }
  }
}
