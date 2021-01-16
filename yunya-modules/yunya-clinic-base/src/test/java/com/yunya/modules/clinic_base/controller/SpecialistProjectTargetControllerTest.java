package com.yunya.modules.clinic_base.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.domain.model.SpecialistProjectTargetModel;
import com.yunya.feign.clinic_base.domain.model.TargetOfMonthModel;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectTargetQuery;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectWorkGoalQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectWorkGoalVO;
import com.yunya.feign.clinic_base.domain.vo.TargetOfMonthVO;
import com.yunya.feign.report.domain.query.SpecialistProjectTargetCompletedInfoQuery;
import com.yunya.feign.report.domain.vo.SpecialistProjectCompletedInfoVO;
import com.yunya.framework.common.model.ResponseResult;
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
 * @date: 2020/12/23 12:42
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SpecialistProjectTargetControllerTest {

  @Autowired private SpecialistProjectTargetController specialistProjectTargetController;

  @Test
  public void save() {
    SpecialistProjectTargetModel model = new SpecialistProjectTargetModel();
    model.setSpecialistProjectId(1);
    model.setBusinessYear("2020");
    model.setBelongType((byte) 0);
    model.setUnit("个");
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
    model.setSpecialistProjectTargetOfMonthModels(list);
    specialistProjectTargetController.saveSpecialistProjectTarget(model);
  }

  @Test
  public void find() {
    SpecialistProjectTargetQuery query = new SpecialistProjectTargetQuery();
    query.setSpecialistProjectId(1);
    query.setBusinessYear("2020");
    query.setBelongType((byte) 0);
    query.setBelongId(35);
    ResponseResult<List<TargetOfMonthVO>> list =
        specialistProjectTargetController.findSpecialistProjectTargetList(query);
    System.out.println(list);
  }

  @Test
  public void find1() {
    SpecialistProjectWorkGoalQuery query = new SpecialistProjectWorkGoalQuery();
    query.setDateType((byte) 0);
    query.setStartDate("2020-10");
    query.setEndDate("2020-12");
    query.setBelongType((byte) 0);
    query.setBelongIds(new Integer[] {35, 42});
    query.setSpecialistProjectIds(new Integer[] {2, 4});
    query.setWhetherPage(true);
    query.setPageNum(1);
    query.setPageSize(10);
    ResponseResult<PageInfo<SpecialistProjectWorkGoalVO>> list =
        specialistProjectTargetController.specialistProjectWorkGoalList(query);
    System.out.println(list);
  }

  @Test
  public void find2() {
    SpecialistProjectTargetCompletedInfoQuery query =
        new SpecialistProjectTargetCompletedInfoQuery();
    query.setOrgId(35);
    query.setStartDate("2020-10");
    query.setEndDate("2020-12");
    ResponseResult<List<SpecialistProjectCompletedInfoVO>> result =
        specialistProjectTargetController.specialistProjectTargetCompletedInfo(query);
    System.out.println(result);
  }
}
