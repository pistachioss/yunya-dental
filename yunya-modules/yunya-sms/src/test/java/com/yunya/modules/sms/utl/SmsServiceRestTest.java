package com.yunya.modules.sms.utl;

import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.redis.util.RedisUtils;
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
    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void init() {
        smsServiceRest.initAutoSendEvent(110);
    }


    @Test
    public void clear() {
        redisUtils.delete(RedisConstants.SMS_STATISTICS_SURPLUS_ORG +35);
    }
}
