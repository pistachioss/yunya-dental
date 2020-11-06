package com.yunya.modules.employeeattend;

import com.yunya.feign.employee_attend.form.AttendanceAddressSetForm;
import com.yunya.feign.employee_attend.form.AttendanceAddressSetQueryForm;
import com.yunya.feign.employee_attend.model.AttendanceAddressSetModel;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.employeeattend.controller.AttendanceAddressSetController;
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
public class AttendanceAddressSetControllerTest {
    @Autowired
    private AttendanceAddressSetController attendanceAddressSetController;

    @Test
    public void testAdd() {
        BaseContextHandler.setUserID("123456");
        AttendanceAddressSetModel model = new AttendanceAddressSetModel();
        model.setOrgId(22);
        model.setAttendanceRange(100);
        model.setLatitude("111.223");
        model.setLongitude("243.112");
        model.setAttendanceAddress("浙江省杭州市西湖区古墩路167号");
        ResponseResult result = attendanceAddressSetController.add(model);
        System.out.println(result);
    }


    @Test
    public void testUpdate() {
        BaseContextHandler.setUserID("123456");
        AttendanceAddressSetForm form = new AttendanceAddressSetForm();
        form.setOrgId(23);
        form.setAttendanceRange(300);
        form.setLatitude("231.223");
        form.setLongitude("184.112");
        form.setAttendanceAddress("浙江省杭州市西湖区古墩路1327号");
        ResponseResult result = attendanceAddressSetController.update(1, form);
        System.out.println(result);
    }


    @Test
    public void testDelete() {
        ResponseResult result = attendanceAddressSetController.delete(1);
        System.out.println(result);
    }

    @Test
    public void testFindAttendanceAddressSetById() {
        ResponseResult result = attendanceAddressSetController.findAttendanceAddressSetById(2);
        System.out.println(result);
    }

    @Test
    public void testFindAttendanceAddressSetList() {
        AttendanceAddressSetQueryForm queryForm = new AttendanceAddressSetQueryForm();
        queryForm.setWhetherPage(true);
        queryForm.setPageNum(1);
        queryForm.setPageSize(5);
        ResponseResult result = attendanceAddressSetController.findAttendanceAddressSetList(queryForm);
        System.out.println(result);
    }
}
