package com.yunya.feign.system.factory;

import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.domain.LogInfo;
import com.yunya.feign.system.domain.PermissionInfo;
import com.yunya.feign.system.domain.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 简介: 系统服务调用降级处理
 *
 * @author: chow
 * @date: 2020/7/9 12:25
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Component
public class RemoteSystemServiceFallBackFactory implements RemoteSystemServiceFeign {

  @Override
  public UserInfo validate(Map<String, Object> params) {
    return null;
  }

  @Override
  public Set<String> selectPermsByUserId(Integer userId) {
    return null;
  }

  @Override
  public List<PermissionInfo> getPermissionByUsername(String uniqueName) {
    log.error("调用{}异常{}", "getPermissionByUsername", uniqueName);
    return null;
  }

  @Override
  public List<PermissionInfo> getAllPermissionInfo() {
    log.error("调用{}异常", "getPermissionByUsername");
    return null;
  }

  @Override
  public void saveLog(LogInfo log) {}
}
