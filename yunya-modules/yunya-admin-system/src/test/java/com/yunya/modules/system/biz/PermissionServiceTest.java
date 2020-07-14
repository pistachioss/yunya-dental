package com.yunya.modules.system.biz;

import com.yunya.feign.system.vo.UserInfo;
import com.yunya.modules.system.rpc.service.PermissionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/7/13 15:26
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PermissionServiceTest {
  /** 注入对象 */
  @Autowired private PermissionService permissionService;

  /**
   * 测试用户信息校验
   */
  @Test
  public void testValidate() {
    UserInfo info = permissionService.validate("admin", "admin");
    System.out.println(info);
  }
}
