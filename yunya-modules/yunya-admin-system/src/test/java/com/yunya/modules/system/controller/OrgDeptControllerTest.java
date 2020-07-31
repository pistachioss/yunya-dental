package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.system.domain.model.CompanyDepartmentModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介: 组织部门控制器测试
 *
 * @author: chow
 * @date: 2020/7/31 09:54
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrgDeptControllerTest {
  /** 注入对象 */
  @Autowired private OrganizationDepartmentController organizationDepartmentController;

  @Test
  public void add() {
    CompanyDepartmentModel resource = new CompanyDepartmentModel();
    resource.setParentId(0);
    resource.setCompanyId(42);
    resource.setDepartmentId(21);
    resource.setOrderNum(1);
    ResponseResult result = organizationDepartmentController.add(resource);
    System.out.println(result);
  }
}
