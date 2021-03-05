package com.yunya.modules.patient_central;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.patient_central.domain.model.PatientBaseInfoModel;
import com.yunya.feign.patient_central.domain.query.PrepaidExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientPrepaymentsInfoVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import com.yunya.modules.patient_central.controller.web.PatientPrepaymentRelationController;
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
 * @date: 2021/1/20 18:00
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PatientPrepaymentRelationControllerTest {
  /***/
  @Autowired private PatientPrepaymentRelationController prepaymentRelationController;
  @Autowired private PatientBaseInfoBiz patientBaseInfoBiz;
  @Test
  public void find() {
    PrepaidExpendRecordQueryForm query = new PrepaidExpendRecordQueryForm();
    query.setPrepaidId("Y002000256");
    query.setPatientId(256);
    ResponseResult result = prepaymentRelationController.expendList(query);
    System.out.println(result.getData());
  }

  @Test
  public void find1() {
    ResponseResult<List<PatientPrepaymentsInfoVo>> result =
        prepaymentRelationController.balancePayment(93459);
    System.out.println(result);
  }

  @Test
  public void test() {
    String str = "{\"id\":null,\"woGuid\":null,\"name\":\"crazyman\",\"mobile\":\"13867185421\",\"faceUrl\":null,\"originType\":null,\"originId\":null,\"sourceId\":null,\"mobileOwner\":123,\"birthday\":null,\"age\":0,\"gender\":0,\"remarks\":null}";
    PatientBaseInfoModel patientBaseInfoModel = JSONObject.parseObject(str,PatientBaseInfoModel.class);
    patientBaseInfoBiz.addPatient(patientBaseInfoModel);
  }

}
