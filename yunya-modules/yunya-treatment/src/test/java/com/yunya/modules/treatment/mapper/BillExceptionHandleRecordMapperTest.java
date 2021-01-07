package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.CurrentMonthBillAdjustQuery;
import com.yunya.feign.treatment.domain.vo.CurrentMonthAdjustBillVO;
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
    List<CurrentMonthAdjustBillVO> vos =
        billExceptionHandleRecordMapper.selectCurrentMonthAdjustBill(query);
    System.out.println(vos);
  }
}
