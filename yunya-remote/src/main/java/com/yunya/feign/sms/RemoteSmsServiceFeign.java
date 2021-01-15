package com.yunya.feign.sms;

import com.yunya.feign.sms.factory.RemoteSmsServiceFallBackFactory;
import com.yunya.feign.sms.model.SmsBatchSendRecordModel;
import com.yunya.feign.sms.model.SmsCommonSendRecordModel;
import com.yunya.feign.sms.model.SmsVerifyCodeModel;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 简介：云牙系统短信服务接口调用
 *
 * @author: chenlin
 * @Description: 云牙系统短信服务接口调用
 * @Date: 2020/12/17 16:40
 * @since: 1.0.0
 */
@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_SMS_SERVICE,
        fallbackFactory = RemoteSmsServiceFallBackFactory.class)
public interface RemoteSmsServiceFeign {
    /**
     * 根据事件code查询关联的模板信息
     *
     * @param eventCode 事件code
     */
    @RequestMapping(value = "/api/sms/findSmsTemplateByEventCode/{eventCode}", method = RequestMethod.GET)
    SmsTemplateSetVO findSmsTemplateByEventCode(@PathVariable(value = "eventCode") String eventCode);

    /**
     * 发送验证码
     *
     * @param smsVerifyCodeModel
     */
    @RequestMapping(value = "/api/sms/sendVerifyCode", method = RequestMethod.POST)
    ResponseResult<T> sendVerifyCode(@RequestBody SmsVerifyCodeModel smsVerifyCodeModel);

    /**
     * 根据主键id查询关联的模板信息
     *
     * @param id 主键id
     */
    @RequestMapping(value = "/api/sms/findSmsTemplateById/{id}", method = RequestMethod.GET)
    SmsTemplateSetVO findSmsTemplateById(@PathVariable(value = "id") Integer id);

    /**
     * 批量发送短信
     *
     * @param smsBatchSendRecordModel
     */
    @RequestMapping(value = "/api/sms/batchSend", method = RequestMethod.POST)
    ResponseResult<T> batchSend(@RequestBody SmsBatchSendRecordModel smsBatchSendRecordModel);

    /**
     * 批量发送短信
     *  @param templateId 模板id
     * @param models
     */
    @RequestMapping(value = "/api/sms/batchSendModels/{templateId}", method = RequestMethod.POST)
    ResponseResult<T> batchSendModels(@PathVariable(value = "templateId") Integer templateId,
                                      @RequestBody @Validated List<? extends SmsCommonSendRecordModel> models);

    /**
     * 初始化生成该门诊的自动发送事件
     *
     * @param orgId
     */
    @RequestMapping(value = "/api/sms/initAutoSendEvent/{orgId}", method = RequestMethod.GET)
    void initAutoSendEvent(@PathVariable(value = "orgId") Integer orgId);
}
