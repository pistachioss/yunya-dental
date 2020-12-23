package com.yunya.modules.sms.utl;

import com.yunya.modules.sms.rpc.SmsServiceRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/22 15:19
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SmsServiceRestTest {

    @Autowired
    private SmsServiceRest smsServiceRest;

    @Test
    public void init() {
        smsServiceRest.initAutoSendEvent(110);
    }
}
