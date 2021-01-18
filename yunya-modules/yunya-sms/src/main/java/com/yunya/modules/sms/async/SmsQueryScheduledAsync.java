package com.yunya.modules.sms.async;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.sms.query.SmsChargeOrderQueryForm;
import com.yunya.feign.sms.query.SmsSendRecordQueryForm;
import com.yunya.feign.sms.query.SmsSignatureSetQueryForm;
import com.yunya.feign.sms.query.SmsTemplateSetQueryForm;
import com.yunya.feign.sms.vo.SmsChargeOrderVO;
import com.yunya.feign.sms.vo.SmsSendRecordVO;
import com.yunya.feign.sms.vo.SmsSignatureSetVO;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.sms.SmsChargeOrder;
import com.yunya.models.sms.SmsSendRecord;
import com.yunya.modules.sms.biz.*;
import com.yunya.modules.sms.enums.SmsApprovalStatusEnum;
import com.yunya.modules.sms.enums.SmsOrderStatusEnum;
import com.yunya.modules.sms.enums.SmsSendStatusEnum;
import com.yunya.modules.sms.utl.AliyunSmsUtl;
import com.yunya.modules.sms.utl.WikiUtl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
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
public class SmsQueryScheduledAsync{
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmsSignatureSetBiz smsSignatureSetBiz;
    @Autowired
    private SmsTemplateSetBiz smsTemplateSetBiz;
    @Autowired
    private SmsChargeOrderBiz smsChargeOrderBiz;
    @Autowired
    private SmsSendRecordBiz smsSendRecordBiz;
    @Autowired
    private SmsOrgStatisticsBiz smsOrgStatisticsBiz;
    /**
     * 两个小时
     */
    private final long expireIn = 3600000 * 2;

    /**
     * 定时任务每天的6点、22点都执行一次：0 0 6,22 * * ?
     */
    @Async("customizeExecutor")
//    @Scheduled(cron = "0 */1 * * * ?")
    @Scheduled(cron = "0 0 22 * * ?")
    public void smsQuerySignatureAsync(){
        log.info("开始同步审核中的短信签名情况");
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
                    log.error("smsQuerySignatureAsync sync error",e);
                }
            });
        }
        log.info("同步审核中的短信签名情况结束");
    }

    /**
     * 同步审核中的短信模板
     */
    @Async("customizeExecutor")
    @Scheduled(cron = "0 0 22 * * ?")
    public void smsQueryTemplateAsync() {
        log.info("开始同步审核中的短信模板情况");
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
                    log.error("smsQueryTemplateAsync sync error",e);
                }
            });
        }
        log.info("同步审核中的短信模板情况结束");
    }

    /**
     * 同步昨天发送中的短信
     */
    @Async("customizeExecutor")
    @Scheduled(cron = "0 0 23 * * ?")
//        @Scheduled(cron = "0 */1 * * * ?")
    public void smsQuerySendDetailsAsync() {
        log.info("开始同步发送中的短信情况");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SmsSendRecordQueryForm recordQueryForm = new SmsSendRecordQueryForm();
        recordQueryForm.setWhetherPage(false);
        recordQueryForm.setSendDate(DateUtil.yesterday());
        recordQueryForm.setStatus(SmsSendStatusEnum.SENDING.getCode());
        List<SmsSendRecordVO> smsSendRecordVOS = smsSendRecordBiz.findSmsSendRecordList(recordQueryForm);
        for (SmsSendRecordVO smsSendRecordVO : smsSendRecordVOS) {
            String mobile = smsSendRecordVO.getMobile();
            String bizId = smsSendRecordVO.getBizId();
            Date crtTime = smsSendRecordVO.getCrtTime();
            String sendDate = sdf.format(crtTime);
            if (StringHelper.isEmpty(bizId)) {
                continue;
            }
            try {
                JSONObject result = AliyunSmsUtl.querySendDetails(mobile, sendDate, "1", "50", bizId);
                if (!"OK".equals(result.getString("Code"))) {
                    log.error("smsQuerySendDetailsAsync query error: {}", result.getString("Message"));
                    continue;
                }
                JSONObject object = result.getJSONObject("SmsSendDetailDTOs").getJSONArray("SmsSendDetailDTO").getJSONObject(0);
                /*短信发送状态，包括：1：等待回执。2：发送失败。3：发送成功。*/
                String sendStatus = object.getString("SendStatus");
                if ("1".equals(sendStatus)) {
                    continue;
                }
                SmsSendRecord smsSendRecord = new SmsSendRecord();
                smsSendRecord.setId(smsSendRecordVO.getId());
                byte status = SmsSendStatusEnum.SEND_SUCC.getCode();
                String bizMsg = object.getString("ErrCode");//错误码
                if ("2".equals(sendStatus)) {
                    status = SmsSendStatusEnum.SEND_FAIL.getCode();
                    smsOrgStatisticsBiz.incrByOrgId(null, smsSendRecordVO.getContentNum(), null, smsSendRecordVO.getOrgId());
                }
                smsSendRecord.setBizMsg(bizMsg);
                smsSendRecord.setStatus(status);
                smsSendRecordBiz.uptSelectiveById(smsSendRecord);
            } catch (Exception e) {
                log.error("smsQuerySendDetailsAsync sync error",e);
            }
        }
        log.info("同步发送中的短信情况结束");
    }

    /**
     * 同步采宝的支付订单的状态，更新短信充值订单状态：
     *  1、超时未支付 -> 关闭
     *  2、等待支付 -> 支付成功/支付失败
     */
    @Async("customizeExecutor")
//    @Scheduled(cron = "0 */1 * * * ?")
    @Scheduled(cron = "0 0 23 * * ?")
    public void wikiQueryOrderAsync() {
        log.info("开始更新短信充值支付记录");
        SmsChargeOrderQueryForm orderQueryForm = new SmsChargeOrderQueryForm();
        orderQueryForm.setWhetherPage(false);
        orderQueryForm.setOrderStatus(SmsApprovalStatusEnum.APPROVALING.getCode());
        List<SmsChargeOrderVO> smsChargeOrderVOS = smsChargeOrderBiz.findSmsChargeOrderList(orderQueryForm);
        if (smsChargeOrderVOS!=null && !smsChargeOrderVOS.isEmpty()) {
            Date now = new Date(System.currentTimeMillis());
            smsChargeOrderVOS.forEach(smsChargeOrderVO -> {
                Date crtTime = smsChargeOrderVO.getCrtTime();
                SmsChargeOrder smsChargeOrder = new SmsChargeOrder();
                try {
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
                    smsChargeOrder.setUptTime(now);
                    smsChargeOrderBiz.uptSelectiveById(smsChargeOrder);
                } catch (Exception e) {
                    log.error("smsQueryAsync update order error",e);
                }
            });
        }
        log.info("更新短信充值支付记录结束");
    }
}
