package com.yunya.middletable.controller;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.middletable.controller.treatment_other.BaseEmployeeScheduleController;
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
 * @Date: 2022/6/14 14:44
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseEmployeeScheduleControllerTest {

    @Autowired
    private BaseEmployeeScheduleController employeeScheduleController;

    @Test
    public void testOperateEmployeeSchedule() {
        String param = "{\"paramMap\":{\"id\":946}, \"operateType\":0, \"msgCategoryEnum\":\"BaseEmployeeSchedule\"}";
        MessageModel model = JSONObject.parseObject(param, MessageModel.class);
        employeeScheduleController.operate(model);
    }
}
