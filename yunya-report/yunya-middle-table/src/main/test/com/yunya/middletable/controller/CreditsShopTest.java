package com.yunya.middletable.controller;

import com.yunya.middletable.service.credits_shop.CreditsShopBiz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2021-04-23 14:06
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class CreditsShopTest {
    @Autowired
    private CreditsShopBiz creditsShopBiz;

    @Test
    public void ivyConsumeAddCreditsTest() {
        BigDecimal bigDecimal = new BigDecimal(1400);
        Integer integer = creditsShopBiz.ivyConsumeAddCredits(10, bigDecimal, 45);
        System.out.println(integer);
    }
}
