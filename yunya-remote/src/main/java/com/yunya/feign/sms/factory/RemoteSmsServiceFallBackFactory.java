package com.yunya.feign.sms.factory;

import com.yunya.feign.sms.RemoteSmsServiceFeign;
import com.yunya.feign.sms.model.SmsBatchSendRecordModel;
import com.yunya.feign.sms.model.SmsCommonSendRecordModel;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.framework.common.model.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 简介：短信服务调用降级处理
 *
 * @author: chenlin
 * @Description: 短信服务调用降级处理
 * @Date: 2020/12/17 16:41
 * @since: 1.0.0
 */
@Slf4j
@Component
public class RemoteSmsServiceFallBackFactory implements RemoteSmsServiceFeign {
    @Override
    public SmsTemplateSetVO findSmsTemplateByEventCode(String eventCode) {
        return null;
    }

    @Override
    public ResponseResult<T> sendVerifyCode(String mobile, String verifyCode, String eventCode) {
        return null;
    }

    @Override
    public SmsTemplateSetVO findSmsTemplateById(Integer id) {
        return null;
    }

    @Override
    public ResponseResult<T> batchSend(SmsBatchSendRecordModel smsBatchSendRecordModel) {
        return null;
    }

    @Override
    public ResponseResult<T> batchSendModels(Integer templateId, List<? extends SmsCommonSendRecordModel> models) {
        return null;
    }
}
