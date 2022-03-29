package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

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
    query.setOrgIds(new Integer[] {26});
    query.setDateType((byte) 0);
    query.setStartDate("2020-03-01");
    query.setEndDate("2020-03-01");
    query.setPayIds(Arrays.asList(23, 26));
    BillDataStatisticsVO vo = billMapper.selectClinicBillDataStatistic(query);
    System.out.println(vo);
  }

  @Test
  public void find1() {
    PatientArrearsCallForQuery query = new PatientArrearsCallForQuery();
    query.setKeyword("wang");
    List<PatientArrearsCallForVO> vos = billMapper.selectPatientArrearsList(query);
    System.out.println(vos);
  }

  @Test
  public void find2() {
    DentistArrearsCallForQuery query = new DentistArrearsCallForQuery();
    List<DentistArrearsCallForVO> vos = billMapper.selectDentistArrearsList(query);
    System.out.println(vos);
  }

  @Test
  public void find3() {
    DentistArrearsDetailQuery query = new DentistArrearsDetailQuery();
    query.setDentistId(526);
    query.setBillStartDate("2020-11-17");
    query.setBillEndDate("2020-11-17");
    query.setKeyword("王一博");
    List<DentistArrearsDetailVO> vos = billMapper.selectDentistArrearsDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void find4() {
    List<PatientArrearsDetailVO> vos = billMapper.selectPatientArrearsDetailList(495);
    System.out.println(vos);
  }

  @Test
  public void find5() {
    StatementStatisticQuery query = new StatementStatisticQuery();
    query.setOrgId(26);
    query.setQueryDate("2021-03");
    CurrentMonthBillStatisticVO vo = billMapper.selectRealBillStatistic(query);
    System.out.println(vo);
  }

  @Test
  public void find6() {
    CurrentMonthBillInfoQuery query = new CurrentMonthBillInfoQuery();
    query.setOrgId(35);
    query.setCurrentMonth("2020-12");
    List<CurrentMonthBillCollectionDebtVO> vos =
        billMapper.selectCurrentMonthBillCollectionDebtList(query);
    System.out.println(vos);
  }

  @Test
  public void find7() {
    BillOfReceivableQuery query = new BillOfReceivableQuery();
    query.setOrgIds(26);
    query.setQueryDate("2021-03-31");
    query.setKeyword("陈凌云");
    query.setRegDentistIds(new Integer[] {341});
    // query.setWhetherPage(false);
    // query.setPageNum(0);
    // query.setPageSize(0);
    // query.setOrderByColumn("");
    // query.setIsAsc("");
    List<BillRestReceivableAmountVO> vos = billMapper.selectBillReceivableAmountList(query);
    System.out.println(vos);
  }
}
