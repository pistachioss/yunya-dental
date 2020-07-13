package com.yunya.modules.system.rpc.service;

import com.yunya.feign.system.domain.UserInfo;
import com.yunya.modules.system.biz.SysUserBiz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介: 系统管理端对外服务业务层
 *
 * @author: chow
 * @date: 2020/7/12 21:34
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionService {

  /** 注入对象 */
  private final SysUserBiz sysUserBiz;

  public PermissionService(SysUserBiz sysUserBiz) {
    this.sysUserBiz = sysUserBiz;
  }

  /**
   * 根据用户名、密码查询用户信息
   *
   * @param username 用户名
   * @param password 密码
   * @return
   */
  public UserInfo validate(String username, String password) {
    return null;
  }
}
