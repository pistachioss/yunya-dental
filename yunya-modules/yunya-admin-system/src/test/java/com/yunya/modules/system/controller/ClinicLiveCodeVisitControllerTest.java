package com.yunya.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.models.system.ClinicLiveCodeVisit;
import com.yunya.modules.system.domain.model.ClinicLiveCodeVisitModel;
import com.yunya.modules.system.domain.query.ClinicLiveCodeVisitQueryForm;
import com.yunya.modules.system.vo.ClinicLiveCodeVisitCountVO;
import com.yunya.modules.system.vo.ClinicLiveCodeVisitVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/6/29 9:39
 * @since: 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClinicLiveCodeVisitControllerTest {

    @Autowired
    private ClinicLiveCodeVisitController clinicLiveCodeVisitController;

    @Test
    public void testClick() {
        ClinicLiveCodeVisitModel model = new ClinicLiveCodeVisitModel();
        model.setOpenId("eaurowanomc");
        model.setNickName("niko");
        model.setIp("192.168.5.234");
        model.setLongitude("124.203509");
        model.setLatitude("33.258371");
        model.setVisitDevice("微信MAC");
        model.setVisitDuration(1581000L);
        model.setVisitTime(new Date());
        model.setIntentionOrgId(30);
        clinicLiveCodeVisitController.click(model);
    }

    @Test
    public void testFindList() {
        String param = "{\"pageNum\":1,\"pageSize\":30,\"startDate\":\"2022-05-01\",\"endDate\":\"2022-07-30\",\"whetherPage\":true}";
        ClinicLiveCodeVisitQueryForm query = JSONObject.parseObject(param, ClinicLiveCodeVisitQueryForm.class);
        PageInfo<ClinicLiveCodeVisitVO> data = clinicLiveCodeVisitController.findList(query).getData();
        System.out.println(JSONObject.toJSON(data));
    }

    @Test
    public void testFindCount() {
        ClinicLiveCodeVisitCountVO data = clinicLiveCodeVisitController.findCount().getData();
        System.out.println(JSONObject.toJSON(data));
    }
}
