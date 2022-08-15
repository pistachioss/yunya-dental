package com.yunya.modules.sms.utl;

import com.yunya.feign.sms.vo.SmsChargeOrderVO;
import com.yunya.models.sms.SmsChargeOrder;
import com.yunya.modules.sms.biz.SmsChargeOrderBiz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Collections;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/18 17:35
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class WikiUtlTest {

    @Autowired
    private SmsChargeOrderBiz smsChargeOrderBiz;
    @Test
    public void queryOrderTest() {
        System.out.println(WikiUtl.queryOrder("a527eb96-e1e6-4bfa-9270-44c89d95f701", ""));
    }

    @Test
    public void test() {
        SmsChargeOrderVO vo = smsChargeOrderBiz.findSmsChargeOrderById(82);
        smsChargeOrderBiz.syncWikiOrder(Collections.singleton(vo));
    }
}
