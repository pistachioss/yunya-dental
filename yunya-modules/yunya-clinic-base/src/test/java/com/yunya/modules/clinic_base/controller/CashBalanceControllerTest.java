package com.yunya.modules.clinic_base.controller;

import com.yunya.feign.clinic_base.domain.query.PeriodCashQuery;
import com.yunya.framework.common.model.ResponseResult;
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
 * @date: 2021/1/19 09:52
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CashBalanceControllerTest {

  @Autowired private CashBalanceController cashBalanceController;

  @Test
  public void find() {
    PeriodCashQuery query = new PeriodCashQuery();
    query.setOrgId(35);
    query.setSettlementDate("2021-01-18");
    ResponseResult<BigDecimal> result = cashBalanceController.periodCash(query);
    System.out.println(result);
  }
}
