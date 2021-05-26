package com.yunya.modules.treatment.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.treatment.controller.web.BaseOralTariffController;
import com.yunya.modules.treatment.controller.web.BaseTariffController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * 简介: 价目表控制层测试
 *
 * @author: chow
 * @date: 2021/5/19 10:04
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTariffControllerTest {
  @Resource private BaseTariffController baseTariffController;
  @Resource private BaseOralTariffController baseOralTariffController;

  @Test
  public void test1() {
    baseTariffController.operateBaseTariffStatus(644, false);
  }

  @Test
  public void test2() {
    baseOralTariffController.operateBaseOralTariffStatus(1, true);
  }

  @Test
  public void test3() {
    ResponseResult<String> number = baseTariffController.getBaseTariffNumber(1);
    System.out.println(number);
  }

  @Test
  public void test4() {
    ResponseResult<String> number = baseOralTariffController.getBaseOralTariffNumber(1);
    System.out.println(number);
  }
}
