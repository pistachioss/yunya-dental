package com.yunya.modules.patient;

import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.modules.patient_central.biz.PatientOriginBiz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/13 17:43
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest(classes = PatientOriginTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class PatientOriginTest {
    @Autowired
    PatientOriginBiz patientOriginBiz;

    @Test
    public void ceshi(){
        PatientOrigin patientOrigin = new PatientOrigin();
        patientOrigin.setOriginType(1);
        List<PatientOrigin> patientOriginByTypt = patientOriginBiz.findPatientOriginByTypt(patientOrigin);
    }
}
