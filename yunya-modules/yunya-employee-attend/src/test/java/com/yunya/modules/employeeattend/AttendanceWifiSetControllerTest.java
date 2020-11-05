package com.yunya.modules.employeeattend;

import com.yunya.feign.employee_attend.form.AttendanceWifiSetForm;
import com.yunya.feign.employee_attend.form.AttendanceWifiSetQueryForm;
import com.yunya.feign.employee_attend.model.AttendanceWifiSetModel;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.employeeattend.controller.AttendanceWifiSetController;
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
 * @Date: 2020/11/5 14:26
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AttendanceWifiSetControllerTest {
    @Autowired
    private AttendanceWifiSetController attendanceWifiSetController;

    @Test
    public void testAdd() {
        BaseContextHandler.setUserID("123456");
        AttendanceWifiSetModel model = new AttendanceWifiSetModel();
        model.setOrgId(23);
        model.setMacAddress("9C:3D:CF:B0:7F:17");
        model.setWifiName("考勤2-wifi");
        ResponseResult result = attendanceWifiSetController.add(model);
        System.out.println(result);
    }


    @Test
    public void testUpdate() {
        BaseContextHandler.setUserID("123456");
        AttendanceWifiSetForm form = new AttendanceWifiSetForm();
        form.setOrgId(23);
        form.setWifiName("杭州wifi");
        form.setMacAddress("9C:3D:CF:B0:7F:5D");
        ResponseResult result = attendanceWifiSetController.update(1, form);
        System.out.println(result);
    }


    @Test
    public void testDelete() {
        ResponseResult result = attendanceWifiSetController.delete(1);
        System.out.println(result);
    }

    @Test
    public void testFindAttendanceWifiSetById() {
        ResponseResult result = attendanceWifiSetController.findAttendanceWifiSetById(2);
        System.out.println(result);
    }

    @Test
    public void testFindAttendanceWifiSetList() {
        AttendanceWifiSetQueryForm queryForm = new AttendanceWifiSetQueryForm();
        queryForm.setWhetherPage(true);
        queryForm.setPageNum(2);
        queryForm.setPageSize(5);
        ResponseResult result = attendanceWifiSetController.findAttendanceWifiSetList(queryForm);
        System.out.println(result);
    }
}
