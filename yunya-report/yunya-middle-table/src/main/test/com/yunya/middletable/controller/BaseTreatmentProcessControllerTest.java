package com.yunya.middletable.controller;


import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.middletable.controller.report.BaseBillController;
import com.yunya.middletable.controller.report.BaseBillPayController;
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
    @Autowired
    private BaseBillController baseBillController;
    @Autowired
    private BaseBillPayController baseBillPayController;

    @Test
    public void testOperateTreatmentProcess() throws InterruptedException {
        String param = "{\"paramMap\":{\"type\":0,\"id\":463912},\"operateType\":1}";
        MessageModel msg = JSONObject.parseObject(param, MessageModel.class);
        ResponseResult result = baseTreatmentProcessController.operateTreatmentProcess(msg);
        System.out.println(result);
    }

    @Test
    public void testTreatDateStatistics() throws InterruptedException {
        String param = "{\"paramMap\":{\"type\":1,\"id\":432915},\"operateType\":1}";
        MessageModel msg = JSONObject.parseObject(param, MessageModel.class);
        ResponseResult result = baseTreatmentProcessController.treatDateStatistics(msg);
        System.out.println(result);
    }

    @Test
    public void testPullTreatDateStatistics() throws InterruptedException {
        String param = "{\"startDate\":\"2000-01-01\",\"endDate\":\"2021-12-31\"}";
        PullForm form = JSONObject.parseObject(param, PullForm.class);
        ResponseResult result = baseTreatmentProcessController.pullTreatDateStatistics(form);
        System.out.println(result);
    }


    @Test
    public void testPullBillDateStatistics() throws InterruptedException {
        String param = "{\"startDate\":\"2000-01-01\",\"endDate\":\"2021-12-31\"}";
        PullForm form = JSONObject.parseObject(param, PullForm.class);
        ResponseResult result = baseBillController.pullBillDateStatistics(form);
        System.out.println(result);
    }

    @Test
    public void testPayDateStatistics() throws InterruptedException {
        String param = "{\"paramMap\":{\"id\":317670},\"operateType\":1}";
        MessageModel form = JSONObject.parseObject(param, MessageModel.class);
        ResponseResult result = baseBillPayController.payDateStatistics(form);
        System.out.println(result);
    }

    @Test
    public void testPullPayDateStatistics() throws InterruptedException {
        String param = "{\"startDate\":\"2000-01-01\",\"endDate\":\"2021-12-31\"}";
        PullForm form = JSONObject.parseObject(param, PullForm.class);
        ResponseResult result = baseBillPayController.pullPayDateStatistics(form);
        System.out.println(result);
    }
}
