package com.yunya.middletable.controller;


import com.yunya.middletable.service.patient.BasePatientBiz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/3/31 14:57
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BasePatientTest {
    @Autowired
    private BasePatientBiz basePatientBiz;

    @Test
    public void testSavePatientGroupRelation() throws InterruptedException {
        basePatientBiz.savePatientGroupRelation(54);
    }
}
