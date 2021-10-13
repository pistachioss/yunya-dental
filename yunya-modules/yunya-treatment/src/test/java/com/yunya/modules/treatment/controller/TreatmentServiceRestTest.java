package com.yunya.modules.treatment.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.LastTreatmentInfoVO;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordVO;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.controller.rpc.TreatmentServiceRest;
import com.yunya.modules.treatment.controller.web.TreatmentRecordController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/8/17 13:42
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TreatmentServiceRestTest {

  @Autowired private TreatmentRecordController treatmentRecordController;

  @Autowired private TreatmentServiceRest treatmentServiceRest;

  @Test
  public void findOne() {
    TreatmentRecord record = treatmentServiceRest.findTreatmentRecordById(919);
    System.out.println(record);
  }

  @Test
  public void testFindTreatInfo() {
    ResponseResult<TreatmentRecordVO> result = treatmentRecordController.findById(919);
    System.out.println(result);
  }

  @Test
  public void find1() {
    ResponseResult<LastTreatmentInfoVO> result = treatmentRecordController.lastTreatmentInfo(104);
    System.out.println(result);
  }

  @Test
  public void find2() {
    PatientTreatmentRecordQueryForm query = new PatientTreatmentRecordQueryForm();
    query.setPatientId(13282);
    ResponseResult<PageInfo<PatientTreatmentRecordVO>> result =
        treatmentRecordController.patientTreatmentRecordList(query);
    System.out.println(result);
  }

  @Test
  public void find3() {
    BaseContextHandler.setOrgId("26");
    BaseContextHandler.setUserID("635");
    treatmentRecordController.treatmentStatusCount();
  }
}
