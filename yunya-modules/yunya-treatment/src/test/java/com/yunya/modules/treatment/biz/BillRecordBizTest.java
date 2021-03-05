package com.yunya.modules.treatment.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.PatientBillStatistics;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
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
}
