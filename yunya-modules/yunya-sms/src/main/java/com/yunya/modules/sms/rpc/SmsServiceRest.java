package com.yunya.modules.sms.rpc;

import com.yunya.feign.sms.model.SmsSendRecordModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.sms.biz.SmsSendRecordBiz;
import io.swagger.annotations.Api;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 批量发送短信
     *
     * @param smsSendRecordModel 短信发送添加模型
     * @return list
     */
    @RequestMapping(value = "/sms/batchSend", method = RequestMethod.POST)
    public ResponseResult<T> batchSend(@RequestBody @Validated SmsSendRecordModel smsSendRecordModel) {
        return smsSendRecordBiz.batchSend(smsSendRecordModel);
    }

    /**
     * 发送短信验证码
     *
     * @param smsSendRecordModel 短信发送添加模型
     * @return list
     */
    @RequestMapping(value = "/sms/sendVerfyCode", method = RequestMethod.POST)
    public ResponseResult<T> sendVerfyCode(@RequestBody @Validated SmsSendRecordModel smsSendRecordModel) {
        return smsSendRecordBiz.sendVerfyCode(smsSendRecordModel);
    }
}
