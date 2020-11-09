package com.yunya.modules.employeeattend;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.employee_attend.form.AttendanceAddressSetForm;
import com.yunya.feign.employee_attend.form.AttendanceAddressSetQueryForm;
import com.yunya.feign.employee_attend.model.AttendanceAddressSetModel;
import com.yunya.feign.employee_attend.model.AttendanceSetModel;
import com.yunya.feign.employee_attend.model.AttendanceWifiSetModel;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.employeeattend.controller.AttendanceAddressSetController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

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

    @Test
    public void testBatchAdd() {
        BaseContextHandler.setUserID("123456");
        AttendanceSetModel model = new AttendanceSetModel();

        AttendanceAddressSetModel aas1 = new AttendanceAddressSetModel();
        aas1.setOrgId(25);
        aas1.setAttendanceRange(200);
        aas1.setLatitude("91.223");
        aas1.setLongitude("203.112");
        aas1.setAttendanceAddress("浙江省杭州市西湖区古墩路167号");

        AttendanceAddressSetModel aas2 = new AttendanceAddressSetModel();
        aas2.setOrgId(25);
        aas2.setAttendanceRange(400);
        aas2.setLatitude("100.120");
        aas2.setLongitude("010.112");
        aas2.setAttendanceAddress("浙江省杭州市西湖区古墩路384号");
        model.setAttendanceAddressSetModels(Arrays.asList(aas1, aas2));

        AttendanceWifiSetModel aws1 = new AttendanceWifiSetModel();
        aws1.setOrgId(25);
        aws1.setMacAddress("3C:3D:5D:B0:7F:11");
        aws1.setWifiName("考勤10-wifi");

        AttendanceWifiSetModel aws2 = new AttendanceWifiSetModel();
        aws2.setOrgId(25);
        aws2.setMacAddress("5C:4D:1F:B0:7F:D1");
        aws2.setWifiName("考勤11-wifi");
        model.setAttendanceWifiSetModels(Arrays.asList(aws1, aws2));
        System.out.println(JSONObject.toJSON(model).toString());
//        ResponseResult result = attendanceAddressSetController.batchAdd(model);
//        System.out.println(result);
    }

    @Test
    public void testFindOrganizationAttendanceAddressSetList() {
        OrganizationModel model = new OrganizationModel();
        model.setWhetherPage(true);
        model.setPageNum(1);
        model.setPageSize(5);
        ResponseResult result = attendanceAddressSetController.findOrganizationAttendanceAddressSetList(model);
        System.out.println(result);
    }
}
