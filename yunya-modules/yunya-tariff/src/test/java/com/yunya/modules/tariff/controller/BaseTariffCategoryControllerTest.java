package com.yunya.modules.tariff.controller;

import com.yunya.feign.treatment.domain.form.BaseTariffCategoryForm;
import com.yunya.feign.treatment.domain.model.BaseTariffCategoryModel;
import com.yunya.feign.treatment.domain.query.BaseTariffCategoryQueryForm;
import com.yunya.framework.common.model.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介: 价目表分类测试
 *
 * @author: chow
 * @date: 2020/7/31 14:55
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTariffCategoryControllerTest {
  /** 注入对象 */
  @Autowired private BaseTariffCategoryController baseTariffCategoryController;

  @Test
  public void add() {
    BaseTariffCategoryModel model = new BaseTariffCategoryModel();
    model.setName("测试测试");
    model.setNumber("T76283");
    ResponseResult result = baseTariffCategoryController.save(model);
    System.out.println(result);
  }

  @Test
  public void update(){
    BaseTariffCategoryForm form = new BaseTariffCategoryForm();
    form.setName("测试修改");
    form.setNumber("T00123");
    baseTariffCategoryController.modify(21, form);
  }

  @Test
  public void list(){
    BaseTariffCategoryQueryForm queryForm = new BaseTariffCategoryQueryForm();
    ResponseResult list = baseTariffCategoryController.findList(queryForm);
    System.out.println(list);
  }
}
