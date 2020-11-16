package com.yunya.modules.employeeattend;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.employee_attend.form.AttendancePunchRecordForm;
import com.yunya.feign.employee_attend.form.AttendancePunchRecordQueryForm;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.employeeattend.controller.AttendancePunchRecordController;
import com.yunya.modules.employeeattend.controller.BaseScheduleController;
import com.yunya.modules.employeeattend.form.ScheduleForm;
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
 * @Date: 2020/11/5 14:26
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AttendancePunchRecordControllerTest {
    @Autowired
    private AttendancePunchRecordController attendancePunchRecordController;
    @Autowired
    private BaseScheduleController baseScheduleController;

    @Test
    public void testPunch() {
        Date now = new Date(System.currentTimeMillis());
        BaseContextHandler.setUserID("554");
        AttendancePunchRecordForm form = new AttendancePunchRecordForm();
        form.setId(7);
        form.setWifiMacAddress("9C:3D:CF:B0:7F:5D");
        form.setPunchAddress("杭州wifi");
        form.setPunchTime(now);
        form.setPunchStatus((byte) 1);
        ResponseResult result = attendancePunchRecordController.punch(form);
        System.out.println(result);
    }

    @Test
    public void testPunchInfo() {
        BaseContextHandler.setUserID("558");
        AttendancePunchRecordQueryForm queryForm = new AttendancePunchRecordQueryForm();
        queryForm.setMacAddress("9C:3D:CF:B0:7F:5D");
        queryForm.setLongitude("");
        queryForm.setLatitude("");
        ResponseResult result = attendancePunchRecordController.punchInfo(queryForm);
        System.out.println(JSONObject.toJSON(result));
    }

    @Test
    public void testSearch() {
        ScheduleForm queryForm = new ScheduleForm();
        ResponseResult result = baseScheduleController.search(queryForm);
        System.out.println(JSONObject.toJSON(result));
    }
}
