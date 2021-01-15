package com.yunya.modules.clinic_base.controller;

import com.yunya.feign.clinic_base.domain.model.BusinessTargetModel;
import com.yunya.feign.clinic_base.domain.model.TargetOfMonthModel;
import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.clinic_base.domain.query.BusinessTargetQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessGoalCompletedInfoVO;
import com.yunya.feign.clinic_base.domain.vo.TargetOfMonthVO;
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
    Set<TargetOfMonthModel> list = new HashSet<>();
    TargetOfMonthModel o1 = new TargetOfMonthModel();
    o1.setMonthNum((byte) 1);
    o1.setBusinessGoal(BigDecimal.valueOf(100));
    list.add(o1);
    TargetOfMonthModel o2 = new TargetOfMonthModel();
    o2.setMonthNum((byte) 2);
    o2.setBusinessGoal(BigDecimal.valueOf(100));
    list.add(o2);
    TargetOfMonthModel o3 = new TargetOfMonthModel();
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
    ResponseResult<List<TargetOfMonthVO>> result =
        businessTargetController.businessTargetList(query);
    System.out.println(result);
  }

  @Test
  public void find1() {
    BusinessGoalCompletedInfoQuery query = new BusinessGoalCompletedInfoQuery();
    query.setBusinessType((byte) 0);
    query.setOrgId(35);
    query.setStartDate("2020-10");
    query.setEndDate("2020-12");
    ResponseResult<BusinessGoalCompletedInfoVO> result =
        businessTargetController.businessGoalCompletedInfo(query);
    System.out.println(result);
  }
}
