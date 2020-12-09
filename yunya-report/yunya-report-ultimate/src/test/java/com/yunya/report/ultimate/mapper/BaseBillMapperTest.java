package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.vo.BillDataStatisticsVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/12/8 21:01
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseBillMapperTest {

  @Autowired private BaseBillMapper billMapper;

  @Test
  public void find() {
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setOrgIds(new Integer[] {35, 42});
    query.setDateType((byte) 0);
    query.setStartDate("2020-07-01");
    query.setEndDate("2020-12-12");
    BillDataStatisticsVO vo = billMapper.selectClinicBillDataStatistic(query);
    System.out.println(vo);
  }
}
