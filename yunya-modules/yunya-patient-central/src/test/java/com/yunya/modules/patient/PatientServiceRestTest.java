package com.yunya.modules.patient;

import com.yunya.feign.patient_central.domain.query.RechargeCashReceiptQuery;
import com.yunya.modules.patient_central.YunyaPatientApplication;
import com.yunya.modules.patient_central.rpc.PatientServiceRest;
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
@SpringBootTest(classes = YunyaPatientApplication.class)
@RunWith(SpringRunner.class)
public class PatientServiceRestTest {
    @Autowired
    private PatientServiceRest patientServiceRest;

    @Test
    public void test1() {
        RechargeCashReceiptQuery query = new RechargeCashReceiptQuery();
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
        System.out.println(patientServiceRest.sumMemberAndPrepayRechargeCash(query));
    }
}
