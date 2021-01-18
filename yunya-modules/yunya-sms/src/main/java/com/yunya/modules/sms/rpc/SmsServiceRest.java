package com.yunya.modules.sms.rpc;

import com.yunya.feign.sms.model.SmsBatchSendRecordModel;
import com.yunya.feign.sms.model.SmsCommonSendRecordModel;
import com.yunya.feign.sms.model.SmsSendRecordModel;
import com.yunya.feign.sms.model.SmsVerifyCodeModel;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.sms.biz.SmsAutosendEventBiz;
import com.yunya.modules.sms.biz.SmsSendRecordBiz;
import com.yunya.modules.sms.biz.SmsTemplateSetBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    @Autowired
    private SmsAutosendEventBiz smsAutosendEventBiz;

    /**
     * 向不同手机号批量发送短信（内容不同）
     *
     * @param templateId 短信模板id
     * @param models 短信发送添加模型
     * @return
     */
    @CurrentUser
    @ApiOperation(value = "向不同手机号批量发送短信（短信内容不同）")
    @RequestMapping(value = "/sms/batchSendModels/{templateId}", method = RequestMethod.POST)
    public ResponseResult<T> batchSendModels(@PathVariable(value = "templateId") Integer templateId,
                             @RequestBody @Validated List<? extends SmsCommonSendRecordModel> models) {
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        String name = BaseContextHandler.getName();
        return smsSendRecordBiz.batchSendByTemplateId(templateId, userId, name, orgId, models);
    }

    /**
     * 批量发送短信
     *
     * @param batchSendRecordModel 短信发送添加模型
     * @return
     */
    @CurrentUser
    @ApiOperation(value = "批量发送短信")
    @RequestMapping(value = "/sms/batchSend", method = RequestMethod.POST)
    public ResponseResult<T> batchSend(@RequestBody @Validated SmsBatchSendRecordModel batchSendRecordModel) {
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        String name = BaseContextHandler.getName();
        return smsSendRecordBiz.batchSend(orgId, userId, name, batchSendRecordModel);
    }

    /**
     * 发送短信验证码
     *
     * @param smsVerifyCodeModel
     * @return
     */
    @ApiOperation(value = "发送短信验证码")
    @RequestMapping(value = "/sms/sendVerifyCode", method = RequestMethod.POST)
    public ResponseResult<T> sendVerifyCode(@RequestBody @Validated SmsVerifyCodeModel smsVerifyCodeModel) {
        return smsSendRecordBiz.sendVerifyCode(smsVerifyCodeModel);
    }

    /**
     * 批量发送同内容的短信
     *
     * @param smsSendRecordModel 短信发送添加模型
     * @return
     */
    @CurrentUser
    @ApiOperation(value = "批量发送同内容的短信")
    @RequestMapping(value = "/sms/sendRecord", method = RequestMethod.POST)
    public ResponseResult<T> sendRecord(@RequestBody @Validated SmsSendRecordModel smsSendRecordModel) {
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        String name = BaseContextHandler.getName();
        return smsSendRecordBiz.sendRecord(orgId, userId, name, smsSendRecordModel);
    }

    /**
     * 根据事件code查询模板信息
     *
     * @param eventCode 事件模板
     * @return
     */
    @CurrentUser
    @ApiOperation(value = "根据事件code查询模板信息")
    @RequestMapping(value = "/sms/findSmsTemplateByEventCode/{eventCode}", method = RequestMethod.GET)
    public SmsTemplateSetVO findSmsTemplateByEventCode(@PathVariable(value = "eventCode") String eventCode) {
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        return smsTemplateSetBiz.findSmsTemplateByEventCode(eventCode, orgId);
    }


    /**
     * 根据事件code查询模板信息
     *
     * @param id 事件模板
     * @return
     */
    @ApiOperation(value = "根据事件code查询模板信息")
    @RequestMapping(value = "/sms/findSmsTemplateById/{id}", method = RequestMethod.GET)
    public SmsTemplateSetVO findSmsTemplateById(@PathVariable(value = "id") Integer id) {
        return smsTemplateSetBiz.findSmsTemplateSetById(id);
    }

    /**
     * 门诊的初始化短信自动发送事件
     *
     * @param orgId
     * @return
     */
    @ApiOperation(value = "门诊的初始化短信自动发送事件")
    @RequestMapping(value = "/sms/initAutoSendEvent/{orgId}", method = RequestMethod.GET)
    public ResponseResult<T> initAutoSendEvent(@PathVariable(value = "orgId") Integer orgId) {
        return smsAutosendEventBiz.initAutoSendEvent(orgId, true);
    }
}
