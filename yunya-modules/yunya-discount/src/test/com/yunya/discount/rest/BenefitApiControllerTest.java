package com.yunya.discount.rest;

import com.yunya.feign.discount.domain.query.CardSaleCashReceiptQuery;
import com.yunya.modules.discount.ClinicDiscountApplication;
import com.yunya.modules.discount.rpc.BenefitApiController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/1/8 15:53
 * @since: 1.0.0
 */
@SpringBootTest(classes = ClinicDiscountApplication.class)
@RunWith(SpringRunner.class)
public class BenefitApiControllerTest {
    @Autowired
    private BenefitApiController benefitApiController;

    @Test
    public void test1() {
        CardSaleCashReceiptQuery query = new CardSaleCashReceiptQuery();
        query.setOrgId(35);
        query.setPayId(57);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse("2020-12-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        query.setStartDate(date);
        query.setEndDate(new Date());
        System.out.println(benefitApiController.getCardSaleCashReceipt(query));
    }
}
