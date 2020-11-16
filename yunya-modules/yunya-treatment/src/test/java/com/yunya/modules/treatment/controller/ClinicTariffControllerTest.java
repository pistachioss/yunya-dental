package com.yunya.modules.treatment.controller;

import com.yunya.feign.treatment.domain.vo.ClinicTariffVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.treatment.controller.web.ClinicTariffController;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介: 门诊价目表控制层测试
 *
 * @author: chow
 * @date: 2020/11/15 12:55
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ClinicTariffControllerTest {
  @Autowired private ClinicTariffController clinicTariffController;

  @Test
  public void testFind() {
    ResponseResult<ClinicTariffVO> result = clinicTariffController.findById(35, 201090);
    System.out.println(result);
  }

  @Test
  public void testSwitchTariff() {
    ResponseResult<T> result = clinicTariffController.switchClinicTariff(35, 201090);
    System.out.println(result);
  }
}
