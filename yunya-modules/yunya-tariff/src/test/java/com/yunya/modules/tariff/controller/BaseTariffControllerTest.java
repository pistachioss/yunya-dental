package com.yunya.modules.tariff.controller;

import com.yunya.feign.tariff.domain.query.BaseTariffQueryForm;
import com.yunya.framework.common.model.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介: 基础价目表控制器测试
 *
 * @author: chow
 * @date: 2020/8/7 13:37
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTariffControllerTest {
  @Autowired private BaseTariffController baseTariffController;

  @Test
  public void list() {
    BaseTariffQueryForm queryForm = new BaseTariffQueryForm();
    queryForm.setKeyWord("开");
    ResponseResult list = baseTariffController.findList(queryForm);
    System.out.println(list);
  }
}
