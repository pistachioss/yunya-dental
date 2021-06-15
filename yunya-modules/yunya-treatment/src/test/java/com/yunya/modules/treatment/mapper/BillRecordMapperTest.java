package com.yunya.modules.treatment.mapper;

import com.yunya.feign.report.domain.query.BillOfReceivableQuery;
import com.yunya.feign.report.domain.vo.BillRestReceivableAmountVO;
import com.yunya.feign.treatment.domain.query.CompletedWorkGoalQuery;
import com.yunya.feign.treatment.domain.vo.PatientBillPrintInfoVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/12/25 16:34
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillRecordMapperTest {
  @Autowired private BillRecordMapper billRecordMapper;

  @Test
  public void find() {
    CompletedWorkGoalQuery query = new CompletedWorkGoalQuery();
    query.setDateType((byte) 0);
    query.setBusinessDate("2020-12");
    query.setBelongIds(new Integer[] {35, 72});
    BigDecimal decimal = billRecordMapper.selectCompletedActualReceivedAmount(query);
    System.out.println(decimal);
  }

  @Test
  public void find2() {
    CompletedWorkGoalQuery query = new CompletedWorkGoalQuery();
    query.setDateType((byte) 0);
    query.setBusinessDate("2020-12");
    query.setBelongIds(new Integer[] {35, 72});
    BigDecimal decimal = billRecordMapper.selectCompletedWorkloadAmount(query);
    System.out.println(decimal);
  }

  @Test
  public void find3() {
    List<PatientBillPrintInfoVO> vos =
        billRecordMapper.selectBillDetailListByIds(66458, new Integer[] {441328});
    System.out.println(vos);
  }

  @Test
  public void selectDebtList() {
    BillOfReceivableQuery query = new BillOfReceivableQuery();
    query.setOrgId(26);
    query.setQueryDate("2021-03-30");
    List<BillRestReceivableAmountVO> vos = billRecordMapper.selectDebtList(query);
    System.out.println(vos);
  }
}
