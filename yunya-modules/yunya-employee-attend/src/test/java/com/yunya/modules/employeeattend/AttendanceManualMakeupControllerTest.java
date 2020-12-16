package com.yunya.modules.employeeattend;

import com.yunya.feign.employee_attend.model.AttendanceManualMakeupModel;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.employeeattend.controller.AttendanceManualMakeupController;
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
public class AttendanceManualMakeupControllerTest {
    @Autowired
    private AttendanceManualMakeupController attendanceManualMakeupController;

    @Test
    public void testAdd() {
        BaseContextHandler.setUserID("569");
        AttendanceManualMakeupModel model = new AttendanceManualMakeupModel();
        model.setOrgId(35);
        model.setUserId(569);
        model.setMakeupDesc("手动补入说明一下");
        model.setMinute(470);
        model.setType((byte)0);
        model.setMakeupDate(new Date());
        ResponseResult result = attendanceManualMakeupController.update(model);
        System.out.println(result);
    }
}
