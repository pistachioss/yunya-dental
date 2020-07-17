package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.system.Brand;
import com.yunya.modules.system.form.base.BaseForm;
import com.yunya.modules.system.form.query.BrandQueryForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介: 品牌控制器测试
 *
 * @author: chow
 * @date: 2020/7/12 11:24
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BrandControllerTest {
  /** 注入对象 */
  @Autowired private BrandController brandController;

  @Test
  public void testFindBrand() {
    BrandQueryForm queryForm = new BrandQueryForm();
    queryForm.setWhetherPage(false);
    queryForm.setPageNum(1);
    queryForm.setPageSize(2);
    ResponseResult result = brandController.findBrandList(queryForm);
    System.out.println(result);
  }

  /** 测试新增品牌 */
  @Test
  public void testAdd() {
    Brand form = new Brand();
    form.setName("艾牙齿科");
    form.setOrderNum(1);
    brandController.addBrand(form);
  }

  /** 测试品牌编辑 */
  @Test
  public void testEdit() {
    BaseForm form = new BaseForm();
    form.setName("艾牙齿科");
    form.setOrderNum(1);
    form.setInservice(false);
    brandController.editBrand(54, form);
  }
}
