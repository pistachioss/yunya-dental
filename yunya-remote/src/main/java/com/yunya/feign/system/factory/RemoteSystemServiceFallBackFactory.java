package com.yunya.feign.system.factory;

import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.JwtRequestFrom;
import com.yunya.feign.system.vo.LogInfo;
import com.yunya.feign.system.vo.PermissionInfo;
import com.yunya.feign.system.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
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

  /**
   * 登陆
   *
   * @param jwtRequestFrom 参数封装
   * @return
   */
  @Override
  public UserInfo validate(JwtRequestFrom jwtRequestFrom) {
    return null;
  }

  /**
   * 根据用户ID查询权限
   *
   * @param userId 用户ID
   * @return
   */
  @Override
  public Set<String> selectPermsByUserId(Integer userId) {
    return null;
  }

  /**
   * 根据用户名查询权限
   *
   * @param uniqueName 用户名
   * @return
   */
  @Override
  public List<PermissionInfo> getPermissionByUsername(String uniqueName) {
    log.error("调用{}异常{}", "getPermissionByUsername", uniqueName);
    return null;
  }

  /**
   * 查询全部权限
   *
   * @return
   */
  @Override
  public List<PermissionInfo> getAllPermissionInfo() {
    log.error("调用{}异常", "getPermissionByUsername");
    return null;
  }

  /**
   * 保存日志
   *
   * @param log
   */
  @Override
  public void saveLog(LogInfo log) {}
}
