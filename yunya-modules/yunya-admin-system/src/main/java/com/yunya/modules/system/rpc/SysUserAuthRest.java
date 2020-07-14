package com.yunya.modules.system.rpc;

import com.yunya.feign.system.vo.UserInfo;
import com.yunya.modules.system.rpc.service.PermissionService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 简介: 用户鉴权控制器
 *
 * @author: chow
 * @date: 2020/7/12 21:49
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("api")
public class SysUserAuthRest {

  /** 注入服务 */
  private final PermissionService permissionService;

  public SysUserAuthRest(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  /**
   * 根据用户名、密码查询用户信息
   *
   * @param paramMap 参数封装
   * @return UserInfoVO
   */
  @RequestMapping(value = "/user/validate", method = RequestMethod.POST)
  public UserInfo validate(@RequestBody Map<String, String> paramMap) {
    return permissionService.validate(paramMap.get("username"), paramMap.get("password"));
  }
}
