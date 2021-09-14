package com.yunya.middletable.controller;

import com.yunya.feign.report.domain.query.PatientCreditsRecordQuery;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.middletable.controller.report.credits_shop.ShopController;
import com.yunya.middletable.service.credits_shop.CreditsShopBiz;
import com.yunya.middletable.utils.SignTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
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
        /*Integer integer = creditsShopBiz.ivyConsumeAddCredits(1008611, bigDecimal, 45);
        System.out.println(integer);*/
    }

    @Test
    public void duibaAutoLoginControllerTest() {
        ResponseResult<Map<String, String>> url = shopController.duibaAutoLogin("oZRpos5NDKSkcLc9kQs38acsEBLI", "10086");
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

    @Test
    public void consumptionPointsTest() throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        StringBuffer params = new StringBuffer();

        params.append("uid=oZRpos99KdPokKt2k_S6lMtfVUCg%2523107888&");
        params.append("credits=20&");
        params.append("appKey=uyjUSAhoswNscUbAoqcxM2EDsiJ&");
        params.append("timestamp=" + System.currentTimeMillis() + "&");
        params.append("description=sdfsd&");
        params.append("orderNum=sdfewfew&");
        params.append("type=qb&");
        params.append("actualPrice=3&");
        HashMap<String, String> map = new HashMap<>();
        map.put("uid","oZRpos99KdPokKt2k_S6lMtfVUCg%2523107888");
        map.put("credits","20");
        map.put("appKey","uyjUSAhoswNscUbAoqcxM2EDsiJ");
        map.put("timestamp",String.valueOf(System.currentTimeMillis()));
        map.put("description","sdfsd");
        map.put("orderNum","sdfewfew");
        map.put("type","qb");
        map.put("actualPrice","3");
        String sign = SignTool.sign(map);
        params.append("sign=" + sign);


        HttpGet httpGet = new HttpGet("http://localhost:8300/duiba/shop/consumptionPoints?" + params);
        CloseableHttpResponse response = null;

        try {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).setSocketTimeout(5000).setRedirectsEnabled(true).build();
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            log.info("响应状态为: {}",response.getStatusLine());
            if (responseEntity != null) {
                log.info("响应内容长度为：{}",responseEntity.getContentLength());
                log.info("响应内容为：{}", EntityUtils.toString(responseEntity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            if (response != null) {
                response.close();
            }
        }
    }
}
