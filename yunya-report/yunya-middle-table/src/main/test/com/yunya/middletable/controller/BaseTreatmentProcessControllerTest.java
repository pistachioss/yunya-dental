package com.yunya.middletable.controller;


import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.middletable.controller.report.BaseTreatmentProcessController;
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
public class BaseTreatmentProcessControllerTest {
    @Autowired
    private BaseTreatmentProcessController baseTreatmentProcessController;

    @Test
    public void testPullBenefit() throws InterruptedException {
        String param = "{\"startDate\":\"2000-01-01\",\"endDate\":\"2021-12-31\"}";
        PullForm form = JSONObject.parseObject(param, PullForm.class);
        ResponseResult result = baseTreatmentProcessController.pullTreatDateStatistics(form);
        System.out.println(result);
    }
}
