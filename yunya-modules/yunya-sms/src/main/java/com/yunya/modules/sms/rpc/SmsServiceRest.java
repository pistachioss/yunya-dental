package com.yunya.modules.sms.rpc;

import com.yunya.feign.sms.model.SmsBatchSendRecordModel;
import com.yunya.feign.sms.model.SmsCommonSendRecordModel;
import com.yunya.feign.sms.model.SmsSendRecordModel;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.sms.biz.SmsSendRecordBiz;
import com.yunya.modules.sms.biz.SmsTemplateSetBiz;
import io.swagger.annotations.Api;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简介: 短信服务对外接口暴露
 *
 * @author: chenlin
 * @date: 2020/12/14 10:24
 * @description:
 * @since: 1.0.0
 */
@Api("短信服务对外接口暴露")
@RestController
@RequestMapping("api")
public class SmsServiceRest {

    @Autowired
    private SmsSendRecordBiz smsSendRecordBiz;
    @Autowired
    private SmsTemplateSetBiz smsTemplateSetBiz;

    /**
     * 批量发送短信
     *
     * @param templateId 短信模板id
     * @param models 短信发送添加模型
     * @return
     */
    @RequestMapping(value = "/sms/batchSendModels/{templateId}", method = RequestMethod.POST)
    public ResponseResult<T> batchSendModels(@PathVariable(value = "templateId") Integer templateId,
                             @RequestBody @Validated List<? extends SmsCommonSendRecordModel> models) {
        return smsSendRecordBiz.batchSend(templateId, models);
    }

    /**
     * 批量发送短信
     *
     * @param batchSendRecordModel 短信发送添加模型
     * @return
     */
    @RequestMapping(value = "/sms/batchSend", method = RequestMethod.POST)
    public ResponseResult<T> batchSend(@RequestBody @Validated SmsBatchSendRecordModel batchSendRecordModel) {
        return smsSendRecordBiz.batchSend(batchSendRecordModel);
    }

    /**
     * 发送短信验证码
     *
     * @param smsSendRecordModel 短信发送添加模型
     * @return
     */
    @RequestMapping(value = "/sms/sendVerifyCode", method = RequestMethod.POST)
    public ResponseResult<T> sendVerifyCode(@RequestBody @Validated SmsSendRecordModel smsSendRecordModel) {
        return smsSendRecordBiz.sendVerifyCode(smsSendRecordModel);
    }

    /**
     * 根据事件code查询模板信息
     *
     * @param eventCode 事件模板
     * @return
     */
    @RequestMapping(value = "/sms/findSmsTemplateByEventCode/{eventCode}", method = RequestMethod.GET)
    public SmsTemplateSetVO findSmsTemplateByEventCode(@PathVariable(value = "eventCode") String eventCode) {
        return smsTemplateSetBiz.findSmsTemplateByEventCode(eventCode);
    }


    /**
     * 根据事件code查询模板信息
     *
     * @param id 事件模板
     * @return
     */
    @RequestMapping(value = "/sms/findSmsTemplateById/{id}", method = RequestMethod.GET)
    public SmsTemplateSetVO findSmsTemplateById(@PathVariable(value = "id") Integer id) {
        return smsTemplateSetBiz.findSmsTemplateSetById(id);
    }
}
