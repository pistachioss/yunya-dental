package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.system.form.query.SysUserInfoDetailQueryFrom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * 简介: 用户控制器测试
 *
 * @author: chow
 * @date: 2020/7/18 15:43
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SysUserControllerTest {
  /** 注入对象 */
  @Autowired private SysUserController sysUserController;

  @Test
  public void testFindUserListByExample() {
    SysUserInfoDetailQueryFrom form = new SysUserInfoDetailQueryFrom();
    List<Integer> orgIds = new ArrayList<>();
    orgIds.add(21);
    orgIds.add(35);
    List<Integer> postIds = new ArrayList<>();
    postIds.add(33);
    postIds.add(34);
    //form.put("userId",1);
    ResponseResult list = sysUserController.findList(form);
    System.out.println(list);
  }
}
