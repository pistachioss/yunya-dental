package com.yunya.modules.sms.utl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Test
    public void queryOrderTest() {
        System.out.println(WikiUtl.queryOrder("a527eb96-e1e6-4bfa-9270-44c89d95f701", ""));
    }
}
