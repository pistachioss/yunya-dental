package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.StatementBillChargeDetailInfoQuery;
import com.yunya.feign.report.domain.vo.StatementBillChargeDetailVO;
import com.yunya.feign.report.domain.vo.TollDataStatisticsVO;
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
 * @date: 2020/12/9 13:04
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseBillPayMapperTest {
  @Autowired private BaseBillPayMapper billPayMapper;

  @Test
  public void find() {
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setOrgIds(new Integer[] {35, 42, 72});
    query.setDateType((byte) 0);
    query.setStartDate("2020-07-01");
    query.setEndDate("2020-12-12");
    TollDataStatisticsVO vo = billPayMapper.selectClinicTollDataStatistic(query);
    System.out.println(vo);
  }

  @Test
  public void find1() {
    StatementBillChargeDetailInfoQuery query = new StatementBillChargeDetailInfoQuery();
    query.setOrgId(35);
    query.setDateType((byte)1);
    query.setStartDate("2020-12");
    query.setEndDate("2020-12");
    //query.setPatientKeyword("wang");
    //query.setBillNum("ZD00352012020009");
    query.setChargeStartDate("2020-12-01");
    query.setChargeEndDate("2020-12-31");
    List<StatementBillChargeDetailVO> vos = billPayMapper.selectBillChargeDetailInfoList(query);
    System.out.println(vos);
  }
}
