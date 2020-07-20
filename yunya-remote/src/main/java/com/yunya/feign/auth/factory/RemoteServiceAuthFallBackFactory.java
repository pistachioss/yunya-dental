package com.yunya.feign.auth.factory;

import com.yunya.feign.auth.RemoteServiceAuthFeign;
import com.yunya.framework.common.model.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 简介: 权限服务降级处理
 *
 * @author: chow
 * @date: 2020/7/9 12:31
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Component
public class RemoteServiceAuthFallBackFactory implements RemoteServiceAuthFeign {

  @Override
  public ResponseResult getUserPublicKey(String clientId, String secret) {
    return null;
  }
}
