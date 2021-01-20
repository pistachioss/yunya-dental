package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.system.domain.form.CompanyDepartmentForm;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2021/1/19 14:33
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrganizationDepartmentControllerTest {
  /***/
  @Autowired private OrganizationDepartmentController organizationDepartmentController;

  @Test
  public void find() {
    CompanyDepartmentForm form = new CompanyDepartmentForm();
    form.setOrderNum(3);
    form.setParentId(4);
    ResponseResult<T> result = organizationDepartmentController.edit(10, form);
    System.out.println(result);
  }
}
