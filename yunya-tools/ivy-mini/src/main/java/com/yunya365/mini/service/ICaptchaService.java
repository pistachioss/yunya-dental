package com.yunya365.mini.service;


import com.yunya.feign.ivy_mini.domain.form.SmsCaptchaCheckForm;

public interface ICaptchaService {

    /**
     * 发送验证码
     *
     * @param phoneNumber phoneNumber
     */
    void sendSmsCaptcha(String phoneNumber);

    /**
     * 校验验证码
     * @param checkForm checkForm
     */
    void checkPatientSmsCaptcha(SmsCaptchaCheckForm checkForm);

}
