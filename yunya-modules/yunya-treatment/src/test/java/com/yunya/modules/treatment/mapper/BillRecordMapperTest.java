package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.CompletedWorkGoalQuery;
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
}
