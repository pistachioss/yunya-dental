package com.yunya.modules.patient_central;

import com.yunya.feign.patient_central.domain.query.PrepaidExpendRecordQueryForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.patient_central.controller.web.PatientPrepaymentRelationController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

  @Test
  public void find() {
    PrepaidExpendRecordQueryForm query = new PrepaidExpendRecordQueryForm();
    query.setPrepaidId("Y002000256");
    query.setPatientId(256);
    ResponseResult result = prepaymentRelationController.expendList(query);
    System.out.println(result.getData());
  }
}
