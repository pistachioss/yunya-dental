package com.yunya.auth.controller;

import com.yunya.framework.common.model.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/7/13 22:31
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ClientControllerTest {
  /** 注入对象 */
  @Autowired private ClientController clientController;

  @Test
  public void testGetUserPublicKey() throws Exception {
    ResponseResult publicKey = clientController.getUserPublicKey("yunya-gate", "123456");
    System.out.println(publicKey);
  }
}
