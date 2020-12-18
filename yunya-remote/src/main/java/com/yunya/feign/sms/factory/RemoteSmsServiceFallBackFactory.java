package com.yunya.feign.sms.factory;

import com.yunya.feign.sms.RemoteSmsServiceFeign;
import com.yunya.feign.sms.model.SmsSendRecordModel;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.framework.common.model.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

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
    public ResponseResult<T> sendVerifyCode(SmsSendRecordModel smsSendRecordModel) {
        return null;
    }
}
