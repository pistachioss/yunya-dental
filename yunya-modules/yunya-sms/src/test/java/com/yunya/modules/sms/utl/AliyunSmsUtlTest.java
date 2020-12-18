package com.yunya.modules.sms.utl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/16 17:12
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AliyunSmsUtlTest {

    @Test
    public void testQuerySmsSign() {
        JSONObject object = AliyunSmsUtl.querySmsSign("云牙");
//        System.out.println(object);
    }

    @Test
    public void testQuerySmsTemplate() {
        JSONObject object = AliyunSmsUtl.querySmsTemplate("SMS_207345058");
//        System.out.println(object);
    }

    @Test
    public void testSendSms() {
        JSONObject param = new JSONObject();
        param.put("code", "543210");
        AliyunSmsUtl.sendSms("13867185423", "ABC商城", "SMS_206564748",param);
    }

    @Test
    public void testSendBatchSms() {
        JSONArray phoneNumberJson = new JSONArray();
        phoneNumberJson.add("13867185423");
        JSONArray signNameJson = new JSONArray();
        signNameJson.add("ABC商城");
        JSONArray templateParamJson = new JSONArray();
        templateParamJson.add(null);
        AliyunSmsUtl.SendBatchSms(phoneNumberJson,signNameJson,"SMS_206564748",templateParamJson);
    }
}
