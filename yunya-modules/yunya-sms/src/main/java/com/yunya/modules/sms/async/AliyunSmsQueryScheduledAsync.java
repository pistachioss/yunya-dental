package com.yunya.modules.sms.async;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.sms.query.SmsSignatureSetQueryForm;
import com.yunya.feign.sms.query.SmsTemplateSetQueryForm;
import com.yunya.feign.sms.vo.SmsSignatureSetVO;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.modules.sms.biz.SmsSignatureSetBiz;
import com.yunya.modules.sms.biz.SmsTemplateSetBiz;
import com.yunya.modules.sms.enums.SmsApprovalStatusEnum;
import com.yunya.modules.sms.utl.AliyunSmsUtl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 定时任务每天的6点、22点都执行一次：0 0 6,22 * * ?
     */
    @Async("customizeExecutor")
    @Scheduled(cron = "0 0 6,22 * * ?")
    @Transactional
    public void smsQueryAsync(){
        SmsSignatureSetQueryForm queryForm = new SmsSignatureSetQueryForm();
        queryForm.setWhetherPage(false);
        queryForm.setSignStatus(SmsApprovalStatusEnum.APPROVALING.getCode());
        List<SmsSignatureSetVO> smsSignatureSetVOS = smsSignatureSetBiz.findSmsSignatureSetList(queryForm);
        if (smsSignatureSetVOS!=null && !smsSignatureSetVOS.isEmpty()) {
            smsSignatureSetVOS.forEach(smsSignatureSetVO -> {
                JSONObject result = AliyunSmsUtl.querySmsSign(smsSignatureSetVO.getSignName());
                String code = result.getString("Code");
                Byte signStatus = result.getByte("SignStatus");
                if ("OK".equals(code) && !SmsApprovalStatusEnum.APPROVALING.getCode().equals(signStatus)) {
                    smsSignatureSetVO.setSignStatus(signStatus);
                    smsSignatureSetBiz.updateSelectiveById(smsSignatureSetVO);
                }
            });
        }

        SmsTemplateSetQueryForm templateSetQueryForm = new SmsTemplateSetQueryForm();
        templateSetQueryForm.setWhetherPage(false);
        templateSetQueryForm.setTemplateStatus(SmsApprovalStatusEnum.APPROVALING.getCode());
        List<SmsTemplateSetVO> smsTemplateSetVOS = smsTemplateSetBiz.findSmsTemplateSetList(templateSetQueryForm);
        if (smsTemplateSetVOS!=null && !smsTemplateSetVOS.isEmpty()) {
            smsTemplateSetVOS.forEach(smsTemplateSetVO -> {
                JSONObject result = AliyunSmsUtl.querySmsTemplate(smsTemplateSetVO.getTemplateCode());
                String code = result.getString("Code");
                Byte templateStatus = result.getByte("TemplateStatus");
                if ("OK".equals(code) && !SmsApprovalStatusEnum.APPROVALING.getCode().equals(templateStatus)) {
                    smsTemplateSetVO.setTemplateStatus(templateStatus);
                    smsTemplateSetBiz.updateSelectiveById(smsTemplateSetVO);
                }
            });
        }
    }
}
