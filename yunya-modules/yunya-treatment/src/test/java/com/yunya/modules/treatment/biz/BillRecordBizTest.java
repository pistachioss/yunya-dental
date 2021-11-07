package com.yunya.modules.treatment.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.BillOfReceivableQuery;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.StatementStatisticQuery;
import com.yunya.feign.report.domain.vo.BillRestReceivableAmountVO;
import com.yunya.feign.report.domain.vo.CurrentMonthBillStatisticVO;
import com.yunya.feign.report.domain.vo.SpecialistProjectCompletedInfoVO;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.PatientBillStatistics;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.modules.treatment.task.AutoChargeTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/8/28 16:12
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillRecordBizTest {
  @Autowired private BillRecordBiz billRecordBiz;
  @Autowired private TreatmentRecordBiz treatmentRecordBiz;
  @Autowired private OrderDetailBiz orderDetailBiz;
  @Autowired private BillPayRecordBiz billPayRecordBiz;
  @Autowired private AutoChargeTask autoChargeTask;

  @Test
  public void getNum() {
    String number = billRecordBiz.generateBillNumber(21);
    System.out.println(number);
  }

  @Test
  public void test() {
    PatientBillStatistics statistics = billRecordBiz.statisticsBill(106058);
    System.out.println(statistics);
  }

  @Test
  public void test1() {
    String param = "{\"startDate\":null,\"endDate\":null,\"treatStatus\":[],\"orgIds\":[],\"dates\":null,\"patientId\":\"106058\",\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true}";
    PatientTreatmentRecordQueryForm queryForm = JSONObject.parseObject(param,PatientTreatmentRecordQueryForm.class);
    PageInfo<PatientTreatmentRecordVO> resultList =
            treatmentRecordBiz.findPatientTreatList(queryForm);
    System.out.println(JSONObject.toJSON(resultList));
  }

  @Test
  public void test2() {
    String param = "{\"orgIds\":[26,27,28,29],\"dateType\":1,\"startDate\":\"2021-02\",\"endDate\":\"2021-02\"}";
    DataStatisticsQuery query = JSONObject.parseObject(param, DataStatisticsQuery.class);
    PageInfo<SpecialistProjectCompletedInfoVO> resultList = orderDetailBiz.specialistProjectTargetCompletedList(query);
    System.out.println(JSONObject.toJSON(resultList));
  }

  @Test
  public void testRevoke() {
    BaseContextHandler.setOrgId("26");
    BaseContextHandler.setUserID("635");
    BaseContextHandler.setName("测试-chenlin");
    billPayRecordBiz.revoke(280587);
  }


  @Test
  public void testFindDebtList() {
    BillOfReceivableQuery query = new BillOfReceivableQuery();
    query.setPageNum(1);
    query.setPageSize(5);
    query.setOrgId(31);
    query.setBillRecordIds(Arrays.asList(439647));
    query.setQueryDate("2021-02-28");
    long t1 = System.currentTimeMillis();
    PageInfo<BillRestReceivableAmountVO> debtList = billRecordBiz.findDebtList(query);
    long t2 = System.currentTimeMillis();
    System.out.println("耗时：" + (t2 - t1));
    System.out.println("数据：" + JSONObject.toJSON(debtList));
  }

  @Test
  public void testFindCurrentMonthStatementStatistic(){
    StatementStatisticQuery query = new StatementStatisticQuery();
    query.setOrgId(30);
    query.setQueryDate("2021-10");
    CurrentMonthBillStatisticVO currentMonthStatementStatistic = billRecordBiz.findCurrentMonthStatementStatistic(query);
    System.out.println(JSONObject.toJSON(currentMonthStatementStatistic));
  }

  @Test
  public void autoCharge() throws InterruptedException {
    autoChargeTask.autoCharge();
  }
}
