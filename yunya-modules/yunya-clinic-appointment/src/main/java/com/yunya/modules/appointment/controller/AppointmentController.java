package com.yunya.modules.appointment.controller;

import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.vo.EmployeeScheduleResultVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.AppointmentBiz;
import com.yunya.feign.appointment.domain.base.AppointmentBaseForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 患者预约Controller
 *
 * @author yunya-lihuibin
 * @create 2020-07-29 11:22
 * @update yunya-lihuibin    2020-07-29    新建
 */
@Api(tags = "患者预约Controller(禁止使用)")
@RestController
@RequestMapping("appoint")
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
    @ApiOperation(value = "添加预约（有冲突检测）")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult addAppointment(@RequestBody AppointmentBaseForm form) throws ParseException {
        Map<String,Object> responseResult = appointmentBiz.addAppointment(form);
        return ResponseUtil.success(responseResult);
    }


}
