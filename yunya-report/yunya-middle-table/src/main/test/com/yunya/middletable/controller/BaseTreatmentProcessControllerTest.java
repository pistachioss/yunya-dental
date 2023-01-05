package com.yunya.middletable.controller;


import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.middletable.controller.emr.TreatPlanDetailController;
import com.yunya.middletable.controller.report.BaseBillController;
import com.yunya.middletable.controller.report.BaseBillPayController;
import com.yunya.middletable.controller.report.BaseRefundController;
import com.yunya.middletable.controller.report.BaseTreatmentProcessController;
import com.yunya.middletable.dao.report.BaseTreatmentProcessMapper;
import com.yunya.models.report.BaseTreatmentProcess;
import org.apache.poi.ss.formula.functions.T;
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
    @Autowired
    private BaseRefundController baseRefundController;
    @Autowired
    private TreatPlanDetailController treatPlanDetailController;
    @Autowired
    private BaseTreatmentProcessMapper baseTreatmentProcessMapper;

    @Test
    public void testOperateTreatmentProcess() throws InterruptedException {
        String param = "{\"paramMap\":{\"type\":1,\"id\":442360},\"operateType\":1}";
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
        String param = "{\"startDate\":\"2000-01-01\",\"endDate\":\"2022-12-31\"}";
        PullForm form = JSONObject.parseObject(param, PullForm.class);
        ResponseResult result = baseTreatmentProcessController.pullTreatDateStatistics(form);
        System.out.println(result);
    }

    @Test
    public void testPullBillDateStatistics() throws InterruptedException {
        String param = "{\"startDate\":\"2000-01-01\",\"endDate\":\"2022-12-31\"}";
        PullForm form = JSONObject.parseObject(param, PullForm.class);
        ResponseResult result = baseBillController.pullBillDateStatistics(form);
        System.out.println(result);
    }

    @Test
    public void testPullPrivilegeDateStatistics() throws InterruptedException {
        String param = "{\"startDate\":\"2022-01-01\",\"endDate\":\"2022-02-29\"}";
        PullForm form = JSONObject.parseObject(param, PullForm.class);
        ResponseResult result = baseBillController.pullPrivilegeDateStatistics(form);
        System.out.println(result);
    }

    @Test
    public void testOperateBill() throws InterruptedException {
        String param = "{\"paramMap\":{\"id\":64},\"operateType\":1}";
        MessageModel form = JSONObject.parseObject(param, MessageModel.class);
        ResponseResult result = baseBillController.operateBill(form);
        System.out.println(result);
    }


    @Test
    public void testOperateBillPay() throws InterruptedException {
        String param = "{\"paramMap\":{\"id\":168},\"operateType\":1}";
        MessageModel form = JSONObject.parseObject(param, MessageModel.class);
        ResponseResult result = baseBillPayController.operateBillPay(form);
        System.out.println(result);
    }

    @Test
    public void testPayDateStatistics() throws InterruptedException {
        String param = "{\"paramMap\":{\"id\":357843},\"operateType\":1}";
        MessageModel form = JSONObject.parseObject(param, MessageModel.class);
        ResponseResult result = baseBillPayController.payDateStatistics(form);
        System.out.println(result);
    }

    @Test
    public void testPullPayDateStatistics() throws InterruptedException {
        String param = "{\"startDate\":\"2022-01-01\",\"endDate\":\"2022-12-31\"}";
        PullForm form = JSONObject.parseObject(param, PullForm.class);
        ResponseResult result = baseBillPayController.pullPayDateStatistics(form);
        System.out.println(result);
    }

    @Test
    public void testPullRefundDateStatistics() throws InterruptedException {
        String param = "{\"startDate\":\"2022-06-01\",\"endDate\":\"2022-06-31\"}";
        PullForm form = JSONObject.parseObject(param, PullForm.class);
        ResponseResult result = baseRefundController.pullRefundDateStatistics(form);
        System.out.println(result);
    }

    @Test
    public void testOperateRefund() throws InterruptedException {
        String param = "{\"paramMap\":{\"id\":674},\"operateType\":1}";
        MessageModel form = JSONObject.parseObject(param, MessageModel.class);
        ResponseResult result = baseRefundController.operateRefund(form);
        System.out.println(result);
    }

    @Test
    public void testOperateTreatPlanDetailWriteoff() throws InterruptedException {
        String param = "{\"paramMap\":{\"id\":441841},\"operateType\":0}";
        MessageModel form = JSONObject.parseObject(param, MessageModel.class);
        ResponseResult result = treatPlanDetailController.operate(form);
        System.out.println(result);
    }

    @Test
    public void testSelectOneInMonthAndPreTreat() {
        Integer orgId = 26;
        Integer patientId = 108355;
        Integer dentistId = 635;
        Integer treatmentId = 441613;
        String eDate = "2021-07-07";
        String sDate = DateUtil.preDate(eDate, 29);
        long l = System.currentTimeMillis();
        BaseTreatmentProcess preTreat = baseTreatmentProcessMapper.selectOneInMonthAndPreTreat(orgId, patientId, dentistId, treatmentId, sDate, eDate);
        System.out.println(System.currentTimeMillis() - l);
        System.out.println(JSONObject.toJSON(preTreat));
    }

    @Test
    public void testPullUpdateInMonthNextId() throws Exception {
        long l = System.currentTimeMillis();
        ResponseResult<T> responseResult = baseTreatmentProcessController.pullUpdateInMonthNextId();
        System.out.println("耗时："+(System.currentTimeMillis() - l));
    }
}
