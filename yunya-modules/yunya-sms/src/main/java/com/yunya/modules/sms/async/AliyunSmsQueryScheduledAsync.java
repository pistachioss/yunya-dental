package com.yunya.modules.sms.async;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.sms.query.SmsChargeOrderQueryForm;
import com.yunya.feign.sms.query.SmsSignatureSetQueryForm;
import com.yunya.feign.sms.query.SmsTemplateSetQueryForm;
import com.yunya.feign.sms.vo.SmsChargeOrderVO;
import com.yunya.feign.sms.vo.SmsSignatureSetVO;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.models.sms.SmsChargeOrder;
import com.yunya.modules.sms.biz.SmsChargeOrderBiz;
import com.yunya.modules.sms.biz.SmsSignatureSetBiz;
import com.yunya.modules.sms.biz.SmsTemplateSetBiz;
import com.yunya.modules.sms.enums.SmsApprovalStatusEnum;
import com.yunya.modules.sms.enums.SmsOrderStatusEnum;
import com.yunya.modules.sms.utl.AliyunSmsUtl;
import com.yunya.modules.sms.utl.WikiUtl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 简介：查询阿里云短信
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/15 15:17
 * @since: 1.0.0
 */
@Component
@EnableScheduling
public class AliyunSmsQueryScheduledAsync {

    @Autowired
    private SmsSignatureSetBiz smsSignatureSetBiz;
    @Autowired
    private SmsTemplateSetBiz smsTemplateSetBiz;
    @Autowired
    private SmsChargeOrderBiz smsChargeOrderBiz;
    /**
     * 两个小时
     */
    private final long expireIn = 3600000 * 2;

    /**
     * 定时任务每天的6点、22点都执行一次：0 0 6,22 * * ?
     */
    @Async("customizeExecutor")
//    @Scheduled(cron = "0 */1 * * * ?")
    @Scheduled(cron = "0 0 6,22 * * ?")
    @Transactional
    public void smsQueryAsync(){
        //查询阿里云短信签名审核
        SmsSignatureSetQueryForm queryForm = new SmsSignatureSetQueryForm();
        queryForm.setWhetherPage(false);
        queryForm.setSignStatus(SmsApprovalStatusEnum.APPROVALING.getCode());
        List<SmsSignatureSetVO> smsSignatureSetVOS = smsSignatureSetBiz.findSmsSignatureSetList(queryForm);
        if (smsSignatureSetVOS!=null && !smsSignatureSetVOS.isEmpty()) {
            smsSignatureSetVOS.forEach(smsSignatureSetVO -> {
                try {
                    JSONObject result = AliyunSmsUtl.querySmsSign(smsSignatureSetVO.getSignName());
                    String code = result.getString("Code");
                    Byte signStatus = result.getByte("SignStatus");
                    if ("OK".equals(code) && !SmsApprovalStatusEnum.APPROVALING.getCode().equals(signStatus)) {
                        smsSignatureSetVO.setSignStatus(signStatus);
                        smsSignatureSetBiz.uptSelectiveById(smsSignatureSetVO);
                    }
                } catch (Exception e) {

                }
            });
        }

        //查询阿里云短信模板审核
        SmsTemplateSetQueryForm templateSetQueryForm = new SmsTemplateSetQueryForm();
        templateSetQueryForm.setWhetherPage(false);
        templateSetQueryForm.setTemplateStatus(SmsApprovalStatusEnum.APPROVALING.getCode());
        List<SmsTemplateSetVO> smsTemplateSetVOS = smsTemplateSetBiz.findSmsTemplateSetList(templateSetQueryForm);
        if (smsTemplateSetVOS!=null && !smsTemplateSetVOS.isEmpty()) {
            smsTemplateSetVOS.forEach(smsTemplateSetVO -> {
                try {
                    JSONObject result = AliyunSmsUtl.querySmsTemplate(smsTemplateSetVO.getTemplateCode());
                    String code = result.getString("Code");
                    Byte templateStatus = result.getByte("TemplateStatus");
                    if ("OK".equals(code) && !SmsApprovalStatusEnum.APPROVALING.getCode().equals(templateStatus)) {
                        smsTemplateSetVO.setTemplateStatus(templateStatus);
                        smsTemplateSetBiz.uptSelectiveById(smsTemplateSetVO);
                    }
                } catch (Exception e) {

                }
            });
        }

        SmsChargeOrderQueryForm orderQueryForm = new SmsChargeOrderQueryForm();
        orderQueryForm.setWhetherPage(false);
        orderQueryForm.setOrderStatus(SmsApprovalStatusEnum.APPROVALING.getCode());
        List<SmsChargeOrderVO> smsChargeOrderVOS = smsChargeOrderBiz.findSmsChargeOrderList(orderQueryForm);
        if (smsChargeOrderVOS!=null && !smsChargeOrderVOS.isEmpty()) {
            Date now = new Date(System.currentTimeMillis());
            smsChargeOrderVOS.forEach(smsChargeOrderVO -> {
                Date crtTime = smsChargeOrderVO.getCrtTime();
                SmsChargeOrder smsChargeOrder = new SmsChargeOrder();
                JSONObject data = WikiUtl.queryOrder(smsChargeOrderVO.getOrderNo(), smsChargeOrderVO.getCbOrderNo());
                String orderStatus = data.getString("order_status");
                byte status = SmsOrderStatusEnum.CLOSED.getCode();//关闭
                if (now.getTime() - crtTime.getTime() <= expireIn) {//未超时
                    if ("PAY_SUC".equals(orderStatus)) {
                        status = SmsOrderStatusEnum.PAY_SUC.getCode();
                    } else if ("PAY_FAIL".equals(orderStatus)) {
                        status = SmsOrderStatusEnum.PAY_FAIL.getCode();
                    } else if ("PAY_WAIT".equals(orderStatus)) {
                        status = SmsOrderStatusEnum.WAIT_PAY.getCode();
                    }
                }
                smsChargeOrder.setId(smsChargeOrderVO.getId());
                smsChargeOrder.setCbOrderNo(data.getString("cb_order_no"));
                smsChargeOrder.setOutOrderNo(data.getString("out_order_no"));
                smsChargeOrder.setOrderStatus(status);
                smsChargeOrder.setPaymentChannel(data.getString("payment_channel"));
                smsChargeOrderBiz.uptSelectiveById(smsChargeOrder);
            });
        }
    }
}
