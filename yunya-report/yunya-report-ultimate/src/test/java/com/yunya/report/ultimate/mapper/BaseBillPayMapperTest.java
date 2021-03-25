package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.StatementBillChargeDetailInfoQuery;
import com.yunya.feign.report.domain.vo.BillIdAndBillPayIdVO;
import com.yunya.feign.report.domain.vo.StatementBillChargeDetailVO;
import com.yunya.feign.report.domain.vo.TollDataStatisticsVO;
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
    query.setOrgIds(new Integer[] {35, 42});
    query.setDateType((byte) 0);
    query.setStartDate("2020-12-01");
    query.setEndDate("2021-01-18");
    TollDataStatisticsVO vo = billPayMapper.selectClinicTollDataStatistic(query);
    System.out.println(vo);
  }

  @Test
  public void find1() {
    StatementBillChargeDetailInfoQuery query = new StatementBillChargeDetailInfoQuery();
    query.setOrgId(26);
    query.setDateType((byte) 0);
    query.setStartDate("2020-12-06");
    query.setEndDate("2020-12-06");
    // query.setPatientKeyword("wang");
    // query.setBillNum("ZD00352012020009");
    // query.setChargeStartDate("2020-12-01");
    // query.setChargeEndDate("2020-12-31");
    List<StatementBillChargeDetailVO> vos = billPayMapper.selectBillChargeDetailInfoList(query);
    System.out.println(vos);
  }

  @Test
  public void find2() {
    StatementBillChargeDetailInfoQuery query = new StatementBillChargeDetailInfoQuery();
    query.setOrgId(156);
    query.setDateType((byte) 0);
    query.setStartDate("2021-01-18");
    query.setEndDate("2021-01-18");
    // query.setPatientKeyword("wang");
    // query.setBillNum("ZD00352012020009");
    // query.setChargeStartDate("2020-12-01");
    // query.setChargeEndDate("2020-12-31");
    List<StatementBillChargeDetailVO> vos =
        billPayMapper.selectCurrentBillCollectionDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void find3() {
    StatementBillChargeDetailInfoQuery query = new StatementBillChargeDetailInfoQuery();
    query.setOrgId(26);
    query.setDateType((byte) 0);
    query.setStartDate("2020-12-09");
    query.setEndDate("2020-12-09");
    // query.setPatientKeyword("wang");
    // query.setBillNum("ZD00352012020009");
    // query.setChargeStartDate("2020-12-01");
    // query.setChargeEndDate("2020-12-31");
    List<StatementBillChargeDetailVO> vos =
        billPayMapper.selectBillCurrentChargeDebtDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void find4() {
    StatementBillChargeDetailInfoQuery query = new StatementBillChargeDetailInfoQuery();
    query.setOrgId(35);
    query.setDateType((byte) 1);
    query.setStartDate("2020-10");
    query.setEndDate("2021-01");
    // query.setPatientKeyword("wang");
    // query.setBillNum("ZD00352012020009");
    // query.setChargeStartDate("2020-12-01");
    // query.setChargeEndDate("2020-12-31");
    List<StatementBillChargeDetailVO> vos =
        billPayMapper.selectBillOtherChargeDebtDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void find5() {
    StatementBillChargeDetailInfoQuery query = new StatementBillChargeDetailInfoQuery();
    query.setOrgId(42);
    query.setDateType((byte) 1);
    query.setStartDate("2020-10");
    query.setEndDate("2021-01");
    // query.setPatientKeyword("wang");
    // query.setBillNum("ZD00352012020009");
    // query.setChargeStartDate("2020-12-01");
    // query.setChargeEndDate("2020-12-31");
    List<StatementBillChargeDetailVO> vos =
        billPayMapper.selectOtherBillCollectionDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void find6() {
    StatementBillChargeDetailInfoQuery query = new StatementBillChargeDetailInfoQuery();
    query.setOrgId(35);
    query.setDateType((byte) 0);
    query.setStartDate("2021-01-18");
    query.setEndDate("2021-01-18");
    query.setPatientKeyword("wang");
    // query.setBillNum("ZD00352012020009");
    // query.setChargeStartDate("2020-12-01");
    // query.setChargeEndDate("2020-12-31");
    List<StatementBillChargeDetailVO> vos =
        billPayMapper.selectCurrentBillIsAcceptedDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void find7() {
    StatementBillChargeDetailInfoQuery query = new StatementBillChargeDetailInfoQuery();
    query.setOrgId(42);
    query.setDateType((byte) 1);
    query.setStartDate("2020-10");
    query.setEndDate("2021-01");
    // query.setPatientKeyword("wang");
    // query.setBillNum("ZD00352012020009");
    // query.setChargeStartDate("2020-12-01");
    // query.setChargeEndDate("2020-12-31");
    List<StatementBillChargeDetailVO> vos =
        billPayMapper.selectOtherBillIsAcceptedDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void find8() {
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setOrgIds(new Integer[] {26});
    query.setDateType((byte) 0);
    query.setStartDate("2021-03-01");
    query.setEndDate("2021-03-15");
    BigDecimal bigDecimal = billPayMapper.selectTotalReceivedAmount(query);
    System.out.println(bigDecimal);
  }

  @Test
  public void find9() {
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setOrgIds(new Integer[] {26});
    query.setDateType((byte) 0);
    query.setStartDate("2021-03-01");
    query.setEndDate("2021-03-21");
    List<BillIdAndBillPayIdVO> vos = billPayMapper.selectBillIdsAndBillPayIds(query);
    System.out.println(vos);
  }
}
