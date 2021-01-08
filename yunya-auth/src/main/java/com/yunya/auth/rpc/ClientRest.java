package com.yunya.auth.rpc;

import com.yunya.auth.configuration.KeyConfiguration;
import com.yunya.auth.domain.UserAuthResponse;
import com.yunya.auth.service.AuthClientService;
import com.yunya.auth.utils.JwtTokenUtil;
import com.yunya.feign.system.vo.FrontUserInfoVO;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.exception.auth.UserAuthException;
import com.yunya.framework.common.utils.jwt.IJWTInfo;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.auth.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

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
  /** 密钥配置信息 */
  private final KeyConfiguration keyConfiguration;
  @Autowired private JwtTokenUtil jwtTokenUtil;
  private final String USER_TOKEN = RedisConstants.REDIS_KEY_USER_TOKEN;

  private final String USER_ID = RedisConstants.REDIS_KEY_USER_ID;
  /** redis服务 */
  @Autowired
  private RedisUtils redisUtils;

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
    byte[] result = {};
    Client validate = authClientService.validate(clientId, secret);
    if (null != validate) {
      result = keyConfiguration.getUserPubKey();
    }
    return result;
  }

  /**
   * 刷新token
   *
   * @param oldToken 旧的token
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/token/time/refresh", method = RequestMethod.GET)
  public void refresh(@RequestParam("oldToken") String oldToken) throws Exception {
    // 获取缓存中的用户信息
    FrontUserInfoVO userInfo = redisUtils.get(USER_TOKEN + oldToken, FrontUserInfoVO.class);
    if (null == userInfo) {
      throw new UserAuthException("当前token已失效，请重新登陆！");
    }
    // 解析token获取JWT声明信息，重生成token
    IJWTInfo infoFromToken = jwtTokenUtil.getInfoFromToken(oldToken);
    String deviceType = infoFromToken.getDeviceType();
    // 获取token过期时间
    redisUtils.set(USER_TOKEN + oldToken, userInfo, jwtTokenUtil.getExpire(), TimeUnit.SECONDS);
    redisUtils.set(RedisConstants.setKey(USER_ID,deviceType,userInfo.getId()), oldToken, jwtTokenUtil.getExpire(), TimeUnit.SECONDS);
  }

}
