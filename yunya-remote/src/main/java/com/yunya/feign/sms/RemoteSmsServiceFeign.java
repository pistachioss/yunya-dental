package com.yunya.feign.sms;

import com.yunya.feign.sms.factory.RemoteSmsServiceFallBackFactory;
import com.yunya.feign.sms.model.SmsSendRecordModel;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    SmsTemplateSetVO findSmsTemplateByEventCode(@PathVariable(value = "id") String eventCode);

    /**
     * 发送验证码
     *
     * @param smsSendRecordModel
     */
    @RequestMapping(value = "/api/sms/sendVerifyCode", method = RequestMethod.POST)
    ResponseResult<T> sendVerifyCode(@RequestBody SmsSendRecordModel smsSendRecordModel);
}
