package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.AssistantActualWorkloadDetailQuery;
import com.yunya.feign.report.domain.query.BillingItemDetailQuery;
import com.yunya.feign.report.domain.query.BillingItemStatisticsQuery;
import com.yunya.feign.report.domain.query.EmployeePersonalWorkloadDetailQuery;
import com.yunya.feign.report.domain.vo.AssistantActualWorkloadDetailVO;
import com.yunya.feign.report.domain.vo.BillingItemDetailVO;
import com.yunya.feign.report.domain.vo.BillingItemInfoVO;
import com.yunya.feign.report.domain.vo.EmployeePersonalActualWorkloadDetailVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 简介: 账单详情测试
 *
 * @author: chow
 * @date: 2020/12/4 14:48
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseBillDetailMapperTest {

  @Autowired private BaseBillDetailMapper baseBillDetailMapper;

  @Test
  public void testActualWorkloadList() {
    AssistantActualWorkloadDetailQuery query = new AssistantActualWorkloadDetailQuery();
    query.setOrgId(35);
    query.setDateType((byte) 2);
    query.setStartDate("2020");
    query.setEndDate("2020");
    query.setAssistantType((byte) 0);
    query.setAssistantId(573);
    query.setKeyword("wang");
    query.setOrderStartDate("2020-11-09");
    query.setOrderEndDate("2021-01-03");
    List<AssistantActualWorkloadDetailVO> vos =
        baseBillDetailMapper.selectAssistantActualWorkloadDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void findActualDetailList() {
    EmployeePersonalWorkloadDetailQuery query = new EmployeePersonalWorkloadDetailQuery();
    query.setOrgId(35);
    query.setDateType((byte) 0);
    query.setQueryDate("2020-12");
    query.setEmployeeId(526);
    query.setKeyword("三七");
    List<EmployeePersonalActualWorkloadDetailVO> vos =
        baseBillDetailMapper.selectEmployeePersonalActualWorkloadDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void findBillingItemInfoList() {
    BillingItemStatisticsQuery query = new BillingItemStatisticsQuery();
    query.setOrgId(35);
    query.setDateType((byte) 2);
    query.setStartDate("2020");
    query.setEndDate("2020");
    Set<Integer> set = new HashSet<>();
    set.add(2447);
    query.setCategoryIds(set);
    List<BillingItemInfoVO> vos = baseBillDetailMapper.selectBillingItemInfoList(query);
    System.out.println(vos);
  }

  @Test
  public void findBillingItemDetailList() {
    BillingItemDetailQuery query = new BillingItemDetailQuery();
    query.setOrgId(35);
    query.setDateType((byte)0);
    query.setStartDate("2020-10-01");
    query.setEndDate("2020-12-07");
    query.setItemId(565);
    query.setItemType((byte)0);
    query.setKeyword("二");
    query.setBillNum("ZD00352011280015");

    List<BillingItemDetailVO> vos = baseBillDetailMapper.selectBillingItemDetailList(query);
    System.out.println(vos);
  }
}
