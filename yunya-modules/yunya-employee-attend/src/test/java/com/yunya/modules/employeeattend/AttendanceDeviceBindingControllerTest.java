package com.yunya.modules.employeeattend;

import com.yunya.feign.employee_attend.form.AttendanceDeviceBindingQueryForm;
import com.yunya.feign.employee_attend.model.AttendanceDeviceBindingModel;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.employeeattend.controller.AttendanceDeviceBindingController;
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
public class AttendanceDeviceBindingControllerTest {
    @Autowired
    private AttendanceDeviceBindingController attendanceDeviceBindingController;

    @Test
    public void testDeviceBinding() {
        ResponseResult result = attendanceDeviceBindingController.authorizationCode("13867185406");
        String verifyCode = result.getData().toString();
        BaseContextHandler.setUserID("554");
        AttendanceDeviceBindingModel model = new AttendanceDeviceBindingModel();
        model.setUserId(554);
        model.setDeviceNumber("4543121454");
        model.setMobile("13867185406");
        model.setVerifyCode(verifyCode);
        result = attendanceDeviceBindingController.deviceBinding(model);
        System.out.println(result);
    }

    @Test
    public void testAuthorizationCode() {
        ResponseResult result = attendanceDeviceBindingController.authorizationCode("13867185423");
        System.out.println(result);
    }

    @Test
    public void testFindEmployeeBindingDeviceById() {
        ResponseResult result = attendanceDeviceBindingController.findEmployeeBindingDevice(569);
        System.out.println(result);
    }

    @Test
    public void testFindEmployeeBindingDeviceList() {
        AttendanceDeviceBindingQueryForm queryForm = new AttendanceDeviceBindingQueryForm();
        queryForm.setWhetherPage(true);
        queryForm.setPageNum(2);
        queryForm.setPageSize(5);
        queryForm.setUserName("");
        queryForm.setBindingCount(null);
        queryForm.setStartDate("2020-11-01");
        queryForm.setEndDate("2020-11-06");
        ResponseResult result = attendanceDeviceBindingController.findEmployeeBindingDeviceList(569, queryForm);
        System.out.println(result);
    }

    @Test
    public void testFindAttendanceAddressSetList() {
        AttendanceDeviceBindingQueryForm queryForm = new AttendanceDeviceBindingQueryForm();
        queryForm.setWhetherPage(true);
        queryForm.setPageNum(2);
        queryForm.setPageSize(5);
        queryForm.setUserName("chenlin");
        queryForm.setBindingCount(6);
        queryForm.setStartDate("2020-11-01");//"2020-11-01"
        queryForm.setEndDate("2020-11-06");//"2020-11-06"
        ResponseResult result = attendanceDeviceBindingController.findBindingDeviceEmployeeList(queryForm);
        System.out.println(result);
    }
}
