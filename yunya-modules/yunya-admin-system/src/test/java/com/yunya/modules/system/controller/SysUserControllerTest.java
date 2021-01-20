package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.system.domain.query.SysUserInfoDetailQueryFrom;
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
    //form.setKeyWord("158");
        List<Integer> orgIds = new ArrayList<>();
    //    orgIds.add(21);
        orgIds.add(26);
        form.setOrgIds(orgIds);
    //    List<Integer> postIds = new ArrayList<>();
    //    postIds.add(33);
    //    postIds.add(34);
        List<Integer> groupIds = new ArrayList<>();
        groupIds.add(4);
    //    groupIds.add(4);
        form.setPostGroupIds(groupIds);
    ResponseResult list = sysUserController.findList(form);
    System.out.println(list);
  }

  @Test
  public void findOne() {
    ResponseResult result = sysUserController.findById(1);
    System.out.println(result);
  }
}
