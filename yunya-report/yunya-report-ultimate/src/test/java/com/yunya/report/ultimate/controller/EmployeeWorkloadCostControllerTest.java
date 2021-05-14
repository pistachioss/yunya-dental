package com.yunya.report.ultimate.controller;

import com.yunya.feign.report.domain.model.EmployeeWorkloadCostModel;
import com.yunya.framework.common.model.ResponseResult;
import org.apache.poi.ss.formula.functions.T;
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
 * @date: 2020/11/4 10:10
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class EmployeeWorkloadCostControllerTest {
  @Autowired private EmployeeWorkloadCostController employeeWorkloadCostController;

  @Test
  public void add() {
    EmployeeWorkloadCostModel model = new EmployeeWorkloadCostModel();
    model.setEmployeeId(514);
    model.setEntryStartMonth("2020-10");
    model.setMaterialFee(BigDecimal.valueOf(200));
    model.setProcessingFee(BigDecimal.valueOf(200));
    ResponseResult<T> result = employeeWorkloadCostController.addOrModifyCost(model);
    System.out.println(result);
  }
}
