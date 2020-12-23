package com.yunya.modules.clinic_base.controller;

import com.yunya.feign.clinic_base.domain.model.BusinessTargetModel;
import com.yunya.feign.clinic_base.domain.model.BusinessTargetOfMonthModel;
import com.yunya.feign.clinic_base.domain.query.BusinessTargetQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessTargetOfMonthVO;
import com.yunya.framework.common.model.ResponseResult;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/12/23 10:34
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BusinessTargetControllerTest {

  @Autowired private BusinessTargetController businessTargetController;

  @Test
  public void save() {
    BusinessTargetModel model = new BusinessTargetModel();
    model.setBusinessType((byte) 0);
    model.setBusinessYear("2020");
    model.setBelongType((byte) 0);
    model.setUnit("元");
    Set<BusinessTargetOfMonthModel> list = new HashSet<>();
    BusinessTargetOfMonthModel o1 = new BusinessTargetOfMonthModel();
    o1.setMonthNum((byte) 1);
    o1.setBusinessGoal(BigDecimal.valueOf(100));
    list.add(o1);
    BusinessTargetOfMonthModel o2 = new BusinessTargetOfMonthModel();
    o2.setMonthNum((byte) 2);
    o2.setBusinessGoal(BigDecimal.valueOf(100));
    list.add(o2);
    BusinessTargetOfMonthModel o3 = new BusinessTargetOfMonthModel();
    o3.setMonthNum((byte) 3);
    o3.setBusinessGoal(BigDecimal.valueOf(100));
    list.add(o3);
    model.setBusinessTargetOfMonthModels(list);

    ResponseResult<T> result = businessTargetController.saveBusinessTarget(model);
    System.out.println(result);
  }

  @Test
  public void find() {
    BusinessTargetQuery query = new BusinessTargetQuery();
    query.setBusinessType((byte) 0);
    query.setBusinessYear("2020");
    query.setBelongType((byte) 0);
    query.setBelongId(35);
    ResponseResult<List<BusinessTargetOfMonthVO>> result =
        businessTargetController.businessTargetList(query);
    System.out.println(result);
  }
}
