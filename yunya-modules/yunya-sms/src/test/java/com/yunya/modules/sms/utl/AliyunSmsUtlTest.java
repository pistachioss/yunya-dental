package com.yunya.modules.sms.utl;

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
        JSONObject object = AliyunSmsUtl.querySmsSign("ABC商城");
//        System.out.println(object);
    }
}
