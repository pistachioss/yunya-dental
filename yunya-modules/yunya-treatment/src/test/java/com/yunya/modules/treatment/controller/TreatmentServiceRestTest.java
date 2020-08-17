package com.yunya.modules.treatment.controller;

import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.rpc.TreatmentServiceRest;
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

  @Autowired private TreatmentServiceRest treatmentServiceRest;

  @Test
  public void findOne() {
    TreatmentRecord record = treatmentServiceRest.findTreatmentRecordById(919);
    System.out.println(record);
  }
}
