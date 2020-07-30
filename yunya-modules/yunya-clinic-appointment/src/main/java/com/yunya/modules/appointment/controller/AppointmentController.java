package com.yunya.modules.appointment.controller;

import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.vo.EmployeeScheduleResultVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.AppointmentBiz;
import com.yunya.modules.appointment.form.AppointmentBaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiResponse;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 患者预约Controller
 *
 * @author yunya-lihuibin
 * @create 2020-07-29 11:22
 * @update yunya-lihuibin    2020-07-29    新建
 */
@ApiModel("患者预约Controller")
@RestController
@RequestMapping("appointment")
public class AppointmentController {

    /** 预约服务 */
    @Autowired
    private AppointmentBiz appointmentBiz;
    @Autowired
    EmployeeAttendServiceFeign employeeAttendServiceFeign;

    /**
     * 添加预约（有冲突检测）
     * @param form  预约数据
     * @return
     * @throws ParseException
     */
    @PostMapping("/add")
    public ResponseResult addAppointment(@RequestBody AppointmentBaseForm form) throws ParseException {
        ResponseResult responseResult = appointmentBiz.addAppointment(form);
        return ResponseUtil.success(responseResult);
    }







    @GetMapping("/test")
    public ResponseResult test() throws ParseException {
        AppointmentBaseForm appointmentBaseForm = new AppointmentBaseForm();

        appointmentBaseForm.setPatientId("123456");
        appointmentBaseForm.setPatientName("张三");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        appointmentBaseForm.setAppointDate(sdf.parse("2020-07-13"));
        appointmentBaseForm.setDentistId("1");
        appointmentBaseForm.setOrgId(35);

        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        employeeScheduleQueryForm.setUserId(533);
        employeeScheduleQueryForm.setStartDate(sdf.parse("2020-07-13"));
        employeeScheduleQueryForm.setClinicId(35);
        employeeScheduleQueryForm.setEndDate(sdf.parse("2020-07-14"));

        EmployeeScheduleResultVO list = employeeAttendServiceFeign.findList(employeeScheduleQueryForm);

//        ResponseResult responseResult = appointmentBiz.checkSchedulingTest(appointmentBaseForm);

//        return responseResult;
        return ResponseUtil.success(list);
    }

    @GetMapping("/checkConflictPatientTest")
    public ResponseResult checkConflictPatientTest() throws ParseException {
        AppointmentBaseForm appointmentBaseForm = new AppointmentBaseForm();
        appointmentBaseForm.setOrgId(21);
        appointmentBaseForm.setDentistId("1002002");
        appointmentBaseForm.setPatientId("10010");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = simpleDateFormat.parse("2020-07-29");
        appointmentBaseForm.setAppointDate(parse);
        appointmentBaseForm.setAppointTime("15:00");
        appointmentBaseForm.setAppointDuration(30);
        return appointmentBiz.checkConflictTest(appointmentBaseForm);
    }

    @GetMapping("/checkConflictDentistTest")
    public ResponseResult checkConflictDentistTest() throws ParseException {
        AppointmentBaseForm appointmentBaseForm = new AppointmentBaseForm();
        appointmentBaseForm.setOrgId(21);
        appointmentBaseForm.setDentistId("1002002");
        appointmentBaseForm.setPatientId("10011");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = simpleDateFormat.parse("2020-07-29");
        appointmentBaseForm.setAppointDate(parse);
        appointmentBaseForm.setAppointTime("15:00");
        appointmentBaseForm.setAppointDuration(30);
        return appointmentBiz.checkConflictTest(appointmentBaseForm);
    }


}
