package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.BillRefundRecordQuery;
import com.yunya.feign.treatment.domain.query.CurrentMonthBillAdjustQuery;
import com.yunya.feign.treatment.domain.vo.BillAdjustRecordVO;
import com.yunya.feign.treatment.domain.vo.BillOfRefundRecordVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2021/1/6 17:42
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillExceptionHandleRecordMapperTest {
  /***/

  @Autowired private BillExceptionHandleRecordMapper billExceptionHandleRecordMapper;

  @Test
  public void find() {
    CurrentMonthBillAdjustQuery query = new CurrentMonthBillAdjustQuery();
    query.setOrgId(35);
    query.setCurrentMonth("2021-01");
    List<BillAdjustRecordVO> vos =
        billExceptionHandleRecordMapper.selectCurrentMonthAdjustBill(query);
    System.out.println(vos);
  }

  @Test
  public void find1() {
    BillRefundRecordQuery query = new BillRefundRecordQuery();
    query.setOrgId(35);
    // query.setKeyWord();
    // query.setBillNum("ZD00352010270003");
    query.setRefundStartDate("2020-10-01");
    query.setRefundEndDate("2020-12-31");
    // query.setBillStartDate("2020-10-08");
    // query.setBillEndDate("2020-12-10");
    query.setRegDentistIds(new Integer[] {521});
    List<BillOfRefundRecordVO> vos =
        billExceptionHandleRecordMapper.selectBillRefundRecordList(query);
    System.out.println(vos);
  }
}
