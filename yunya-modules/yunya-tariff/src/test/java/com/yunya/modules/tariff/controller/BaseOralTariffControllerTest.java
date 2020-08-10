package com.yunya.modules.tariff.controller;

import com.yunya.framework.common.model.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介: 基础商品项目控制层测试
 *
 * @author: chow
 * @date: 2020/8/7 13:26
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseOralTariffControllerTest {
  @Autowired private BaseOralTariffController baseOralTariffController;

  @Test
  public void findById() {
    ResponseResult result = baseOralTariffController.findById(89);
    System.out.println(result);
  }
}
