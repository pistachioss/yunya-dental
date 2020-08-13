package com.yunya.modules.appointment.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.AppointStatusForm;
import com.yunya.feign.appointment.domain.form.AppointmentBaseForm;
import com.yunya.feign.appointment.domain.form.AppointmentCancelCauseForm;
import com.yunya.feign.appointment.domain.query.AppointDentistListByDateQuery;
import com.yunya.feign.appointment.domain.query.AppointmentPatientDimensionByDayQuery;
import com.yunya.feign.appointment.vo.AppointmentDentistDimensionVo;
import com.yunya.feign.appointment.vo.AppointmentDimensionVo;
import com.yunya.feign.appointment.vo.AppointmentVo;
import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.Appointment;
import com.yunya.modules.appointment.biz.AppointmentBiz;
import com.yunya.feign.appointment.domain.model.AppointmentBaseModel;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * 患者预约中心Controller
 *
 * @author yunya-lihuibin
 * @create 2020-07-29 11:22
 * @update yunya-lihuibin    2020-07-29    新建
 */
@Api(tags = "患者预约中心Controller(开发中)")
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
    public ResponseResult addAppointment(@RequestBody @Validated AppointmentBaseModel form) throws ParseException {
        ResponseResult responseResult = appointmentBiz.addAppointment(form);
        return responseResult;
    }

    /**
     * 新增预约（继续添加）
     *
     * @description 出现预约冲突后继续添加患者预约
     * @param appointmentForm 预约Form表单
     */
    @ApiOperation(value = "新增预约（预约冲突后继续添加）")
    @PostMapping("/add/continue")
    @CurrentUser
    public ResponseResult continueAddAppointment(
            @RequestBody @Validated AppointmentBaseModel appointmentForm) {

        ResponseResult responseResult = appointmentBiz.continueAddAppointment(appointmentForm);
        return responseResult;
    }

    /**
     * 修改预约（冲突检测）
     * @param form 修改表单
     * @return ResponseResult
     */
    @ApiOperation(value = "修改预约（冲突检测）")
    @PutMapping("/update")
    @CurrentUser
    public ResponseResult updateAppointment(@RequestBody @Validated AppointmentBaseForm form){
        ResponseResult responseResult = appointmentBiz.updateAppointment(form);
        return responseResult;
    }

    /**
     * 修改预约（继续保存）
     * @param appointmentForm 修改预约表单
     * @return  ResponseResult
     */
    @ApiOperation(value = "修改预约（继续保存）")
    @PutMapping("/update/continue")
    @CurrentUser
    public ResponseResult editAppointmentContinueSave(@RequestBody @Validated AppointmentBaseForm appointmentForm){
        return ResponseUtil.success(appointmentBiz.continueUpdateAppointment(appointmentForm));
    }

    /**
     * 修改预约状态
     * @param id 预约id
     * @param form 预约表单
     * @return  ResponseResult
     */
    @ApiOperation(value = "修改预约状态")
    @PutMapping("/update/appoint_status/{id}/{appointStatus}")
    @CurrentUser
    public ResponseResult updateAppointStatus(
            @PathVariable("id") Integer id, @RequestBody @Validated AppointStatusForm form){
        Appointment appointment = appointmentBiz.updateAppointStatus(id, form.getAppointStatus(),form.getRemarks());
        return ResponseUtil.success(appointment);
    }

    /**
     * 取消预约/删除预约（逻辑删除）
     * @param id 预约id
     * @param form 取消预约原因表单
     * @return  ResponseResult
     */
    @ApiOperation(value = "修改预约状态")
    @PutMapping("/delete/appoint/{id}")
    @CurrentUser
    public ResponseResult AppointmentCancel(
            @PathVariable("id") Integer id,
           @RequestBody @Validated AppointmentCancelCauseForm form){
        ResponseResult responseResult = appointmentBiz.appointmentCancel(id, form);
        return responseResult;
    }

    /**
     * 确认预约（就诊画面用）
     * @param id 预约id
     * @return  ResponseResult
     */
    @ApiOperation(value = "确认预约（就诊画面用）")
    @PutMapping("/confirm/{id}")
    @CurrentUser
    public ResponseResult appointConfirm(@PathVariable("id") Integer id){
        Integer result = appointmentBiz.confirmAppointment(id);
        return ResponseUtil.success();
    }

    /**
     * 根据id查询预约
     * @param id  预约id
     * @return
     */
    @ApiOperation(value = "根据id查询预约")
    @GetMapping("/find/{id}")
    public ResponseResult findAppointById(Integer id){
        AppointmentVo appointmentVo = appointmentBiz.findAppointmentById(id);
        return ResponseUtil.success(appointmentVo);
    }

    /**
     * 根据条件查询预约可视图（患者维度）
     * @param query  查询参数
     * @return
     */
    @ApiOperation(value = "根据条件查询患者维度预约可视图")
    @PostMapping("/find/patient/dimension")
    public ResponseResult findAppointmentPatientDimensionByDate(@RequestBody @Validated AppointmentPatientDimensionByDayQuery query){
        if (query.getWhetherPage()){
            PageHelper.offsetPage(query.getPageNum(),query.getPageSize());
        }
        List<AppointmentDimensionVo> appointmentDimensionVos = appointmentBiz.findAppointmentPatientDimensionByExample(query);
        PageInfo<AppointmentDimensionVo> pageInfo = new PageInfo<>(appointmentDimensionVos);
        return ResponseUtil.success(pageInfo);
    }


    /**
     * 根据条件查询预约可视图（医生维度）
     * @param query  查询参数
     * @return
     */
    @ApiOperation(value = "根据条件查询医生维度预约可视图")
    @PostMapping("/find/dentist/dimension")
    public ResponseResult findAppointmentDentistDimensionByExample(@RequestBody AppointDentistListByDateQuery query){

        List<AppointmentDentistDimensionVo> appointmentDentistDimensionByExample = appointmentBiz.findAppointmentDentistDimensionByExample(query);
        return ResponseUtil.success(appointmentDentistDimensionByExample);
    }






}
