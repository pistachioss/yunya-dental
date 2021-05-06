package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.StatementStatisticQuery;
import com.yunya.feign.report.domain.vo.CurrentMonthBillStatisticVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2021/1/5 16:37
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CurrentMonthBillStatisticsMapperTest {

  @Autowired private CurrentMonthBillStatisticsMapper currentMonthBillStatisticsMapper;

  @Test
  public void find1() {
    StatementStatisticQuery query = new StatementStatisticQuery();
    query.setOrgId(26);
    query.setQueryDate("2021-03");
    CurrentMonthBillStatisticVO vo =
        currentMonthBillStatisticsMapper.selectCurrentMonthBillStatistics(query);
    System.out.println(vo);
  }
}
