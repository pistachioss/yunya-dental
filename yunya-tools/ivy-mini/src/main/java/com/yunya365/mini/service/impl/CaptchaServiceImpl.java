package com.yunya365.mini.service.impl;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.yunya.feign.ivy_mini.domain.bo.SmsCacheBO;
import com.yunya.feign.ivy_mini.domain.form.SmsCaptchaCheckForm;
import com.yunya.feign.sms.RemoteSmsServiceFeign;
import com.yunya.feign.sms.model.SmsVerifyCodeModel;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.constant.StringPool;
import com.yunya.framework.common.enums.SmsAutosendEventEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya365.mini.service.ICaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

import static com.yunya.framework.common.constant.RedisConstants.*;
import static com.yunya365.mini.enums.IvyMiniError.*;

/**
 * @description:
 * @author: xy
 * @date 2021/11/15 13:42
 **/
@Service
@Slf4j
public class CaptchaServiceImpl implements ICaptchaService {

    @Resource(name = "getCoSmsClient")
    private CCPRestSmsSDK coSmsClient;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RemoteSmsServiceFeign smsServiceFeign;
    @Value("${spring.profiles.active}")
    private String env;

    @Override
    public void sendSmsCaptcha(String phoneNumber) {
        //获取当前号码redis数据
        String captchaKey = RedisConstants.buildLockCacheKey(MINI_CAPTCHA, phoneNumber);
        SmsCacheBO smsCaptcha = redisUtils.get(captchaKey, SmsCacheBO.class);
        if (Objects.nonNull(smsCaptcha)) {
            LocalDateTime createTime = smsCaptcha.getCreateTime();
            LocalDateTime now = LocalDateTime.now();
            long between = Duration.between(createTime, now).getSeconds();
            //redis间隔不超过一分钟，给出提示
            if (between < 60) {
                throw ClientServiceException.wrap(GAP_CAPTCHA_RETRY.getCode(), GAP_CAPTCHA_RETRY.getMessage(), 60 - between);
            }
        }
        String captcha = this.buildSmsCaptcha();
        ResponseResult result = sendCaptcha(phoneNumber, captcha);
        if (Objects.equals(0, result.getStatus())) {
            SmsCacheBO smsCacheBo = SmsCacheBO.builder().
                    captcha(captcha).phoneNumber(phoneNumber)
                    .createTime(LocalDateTime.now()).build();
            //验证码存入redis
            redisUtils.set(captchaKey, smsCacheBo, 10 * 60);
            log.info("验证码结果result：{}，缓存value：{}", result, smsCacheBo);
        } else {
            log.error("验证码错误：{}", result);
            throw ClientServiceException.wrap(THIRD_PARTY_ERROR);
        }
    }

    @Override
    public void checkPatientSmsCaptcha(SmsCaptchaCheckForm checkForm) {
        validContext(checkForm);
        //todo 校验患者是否已经绑定

    }

    /**
     * 校验短信内容
     *
     * @param checkForm:
     * @return void
     */
    private void validContext(SmsCaptchaCheckForm checkForm) {
        String phoneNumber = checkForm.getPhoneNumber();
        String captchaKey = RedisConstants.buildLockCacheKey(MINI_CAPTCHA, phoneNumber);
        //获取redis验证
        SmsCacheBO smsCaptcha = redisUtils.get(captchaKey, SmsCacheBO.class);
        //验证码是否过期
        if (Objects.isNull(smsCaptcha)) {
            throw ClientServiceException.wrap(SMS_EXPIRED);
        }
        //验证码是否正确
        if (!checkForm.getCaptcha().toLowerCase().equalsIgnoreCase(smsCaptcha.getCaptcha())) {
            throw ClientServiceException.wrap(CAPTCHA_NOT_MATCH);
        }
    }

    private ResponseResult sendCaptcha(String phoneNumber, String captcha) {
        ResponseResult result;
        if (!Objects.equals(StringPool.PROD, env)) {
            result = ResponseUtil.success();
        } else {
            SmsVerifyCodeModel smsVerifyCodeModel = new SmsVerifyCodeModel();
            smsVerifyCodeModel.setMobile(phoneNumber);
            smsVerifyCodeModel.setVerifyCode(captcha);
            smsVerifyCodeModel.setEventCode(SmsAutosendEventEnum.FORGET_PASSWORD.getCode());
            //调用第三方验证码接口
            result = smsServiceFeign.sendVerifyCode(smsVerifyCodeModel);
        }
        return result;
    }

    private String buildSmsCaptcha() {
        String captcha;
        // 开发环境
        if (!StringPool.PROD.equals(env)) {
            captcha = "123456";
        }
        // 生产环境
        else {
            captcha = String.valueOf(new Random().nextInt(900000) + 100000);
        }
        return captcha;
    }
}
