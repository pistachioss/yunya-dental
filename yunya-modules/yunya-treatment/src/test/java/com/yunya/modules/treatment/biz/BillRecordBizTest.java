package com.yunya.modules.treatment.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.vo.SpecialistProjectCompletedInfoVO;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.PatientBillStatistics;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.framework.common.context.BaseContextHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
  @Autowired BillRecordBiz billRecordBiz;
  @Autowired private TreatmentRecordBiz treatmentRecordBiz;
  @Autowired private OrderDetailBiz orderDetailBiz;
  @Autowired BillPayRecordBiz billPayRecordBiz;

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
}
