package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.system.form.query.OrganizationQueryForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介: 组织控制器测试
 *
 * @author: chow
 * @date: 2020/7/15 14:45
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrgControllerTest {

  /** 注入对象 */
  @Autowired private OrganizationController organizationController;

  /** 根据条件查询组织列表信息 */
  @Test
  public void testFindList() {
    OrganizationQueryForm form = new OrganizationQueryForm();
     form.setTypes(new Byte[] {0, 2});
    form.setWhetherPage(false);
    form.setPageSize(3);
    form.setPageNum(2);
    form.setName("门诊");
    ResponseResult list = organizationController.findList(form);
    System.out.println(list);
  }
}
