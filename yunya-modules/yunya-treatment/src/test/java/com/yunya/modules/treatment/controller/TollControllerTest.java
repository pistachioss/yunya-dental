package com.yunya.modules.treatment.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.treatment.controller.web.TollController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2021/1/19 14:04
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TollControllerTest {
  /***/
  @Autowired private TollController tollController;

  @Test
  public void find() {
    ResponseResult<BigDecimal> result = tollController.restPrepaidAmount(471);
    System.out.println(result);
  }
}
