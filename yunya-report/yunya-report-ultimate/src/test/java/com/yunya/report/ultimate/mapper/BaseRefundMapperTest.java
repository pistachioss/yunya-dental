package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.AssistantRefundDetailQuery;
import com.yunya.feign.report.domain.query.EmployeePersonalWorkloadDetailQuery;
import com.yunya.feign.report.domain.query.StatementBillRefundDetailInfoQuery;
import com.yunya.feign.report.domain.vo.AssistantRefundDetailVO;
import com.yunya.feign.report.domain.vo.EmployeePersonalRefundWorkloadDetailVO;
import com.yunya.feign.report.domain.vo.StatementBillRefundDetailVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介: 账单退费测试
 *
 * @author: chow
 * @date: 2020/12/4 14:01
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseRefundMapperTest {

  @Autowired private BaseRefundMapper refundMapper;

  @Test
  public void findRefundList() {
    AssistantRefundDetailQuery query = new AssistantRefundDetailQuery();
    query.setOrgId(35);
    query.setDateType((byte) 0);
    query.setStartDate("2020-10-01");
    query.setEndDate("2020-11-30");
    query.setAssistantType((byte) 2);
    query.setAssistantId(525);
    query.setRefundStartDate("2020-11-30");
    List<AssistantRefundDetailVO> vos = refundMapper.selectAssistantRefundDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void find() {
    StatementBillRefundDetailInfoQuery query = new StatementBillRefundDetailInfoQuery();
    query.setOrgId(35);
    query.setDateType((byte) 0);
    query.setStartDate("2020-10-01");
    query.setEndDate("2021-01-13");
    // query.setPatientKeyword("熊二");
    // query.setBillNum("ZD00352101120001");
    // query.setRefundStartDate("2021-01-12");
    // query.setRefundEndDate("2021-01-12");
    List<StatementBillRefundDetailVO> vos = refundMapper.selectCurrentBillRefundDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void find1() {
    EmployeePersonalWorkloadDetailQuery query = new EmployeePersonalWorkloadDetailQuery();
    query.setStartDate("2021-01");
    query.setEmployeeId(88);
    query.setDateType((byte) 0);
    List<EmployeePersonalRefundWorkloadDetailVO> list =
        refundMapper.selectEmployeePersonalRefundWorkloadDetail(query);
    System.out.println(list);
  }
}
