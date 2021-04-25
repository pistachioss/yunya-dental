package com.yunya.middletable.controller;

import com.yunya.feign.report.domain.query.PatientCreditsRecordQuery;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.middletable.controller.report.credits_shop.ShopController;
import com.yunya.middletable.service.credits_shop.CreditsShopBiz;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2021-04-23 14:06
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class CreditsShopTest {
    @Autowired
    private CreditsShopBiz creditsShopBiz;
    @Autowired
    private ShopController shopController;

    @Test
    public void ivyConsumeAddCreditsTest() {
        BigDecimal bigDecimal = new BigDecimal(1400);
        Integer integer = creditsShopBiz.ivyConsumeAddCredits(1008611, bigDecimal, 45);
        System.out.println(integer);
    }

    @Test
    public void duibaAutoLoginControllerTest() {
        ResponseResult<Map<String, String>> url = shopController.duibaAutoLogin("oZRpos5NDKSkcLc9kQs38acsEBLI", 10086);
        log.info("免登录请求url = {}",url);
    }

    @Test
    public void patientCreditsRecordControllerTest() {
        PatientCreditsRecordQuery query = new PatientCreditsRecordQuery();
        query.setPatientId(1008611);
        query.setWhetherPage(true);
        ResponseResult responseResult = shopController.patientCreditsRecord(query);
        log.info("查询用户积分记录：\n{}",responseResult);
    }

    @Test
    public void lastPatientCreditsControllerTest() {
        ResponseResult responseResult = shopController.lastPatientCredits(1008611);
        log.info("查询用户积分账户:\n",responseResult);
    }
}
