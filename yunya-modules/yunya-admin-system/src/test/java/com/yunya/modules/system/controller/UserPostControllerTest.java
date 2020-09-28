package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.form.EmployeeInfoQueryForm;
import com.yunya.feign.system.vo.EmployeeInfoVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.system.domain.model.SysUserPostModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/7/28 10:45
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserPostControllerTest {
  /** 注入对象 */
  @Autowired private SysUserPostController sysUserPostController;

  @Test
  public void add() {
    SysUserPostModel entity = new SysUserPostModel();
    entity.setCompanyId(21);
    entity.setDepartmentId(20);
    entity.setPostId(33);
    entity.setUserId(521);
    ResponseResult result = sysUserPostController.add(entity);
    System.out.println(result);
  }

  @Test
  public void testFindUserPostList() {
    ResponseResult result = sysUserPostController.postInfoList(42, 514);
    System.out.println(result);
  }

  @Test
  public void findPost() {
    EmployeeInfoQueryForm form = new EmployeeInfoQueryForm();
    form.setOrgIds(new Integer[] {35});
    form.setPostGroupIds(new Integer[] {3});
    ResponseResult<PageInfo<EmployeeInfoVO>> result =
        sysUserPostController.findEmployeeByExample(form);
    System.out.println(result);
  }
}
