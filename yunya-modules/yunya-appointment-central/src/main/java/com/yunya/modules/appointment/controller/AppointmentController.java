package com.yunya.modules.appointment.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.AppointStatusForm;
import com.yunya.feign.appointment.domain.form.AppointmentBaseForm;
import com.yunya.feign.appointment.domain.form.AppointmentCancelCauseForm;
import com.yunya.feign.appointment.domain.query.*;
import com.yunya.feign.appointment.vo.*;
import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.Appointment;
import com.yunya.modules.appointment.biz.AppointmentBiz;
import com.yunya.feign.appointment.domain.model.AppointmentBaseModel;
import com.yunya.modules.appointment.util.pageUtil.PageUtil;
import com.yunya.modules.appointment.util.pageUtil.model.Page;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * 患者预约中心Controller
 *
 * @author yunya-lihuibin
 * @create 2020-07-29 11:22
 * @update yunya-lihuibin 2020-07-29 新建
 */
@Api(tags = "患者预约中心Controller")
@RestController
@RequestMapping("appoint")
public class AppointmentController {

  private Logger logger = LoggerFactory.getLogger(AppointmentController.class);

  /** 预约服务 */
  @Autowired private AppointmentBiz appointmentBiz;

  @Autowired EmployeeAttendServiceFeign employeeAttendServiceFeign;

  /**
   * 添加预约（有冲突检测）
   *
   * @param form 预约数据
   * @return
   * @throws ParseException
   */
  @ApiOperation(value = "添加预约（有冲突检测）")
  @PostMapping("/add")
  @CurrentUser
  public ResponseResult addAppointment(@RequestBody @Validated AppointmentBaseModel form)
      throws ParseException {
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
   *
   * @param form 修改表单
   * @return ResponseResult
   */
  @ApiOperation(value = "修改预约（冲突检测）")
  @PutMapping("/update")
  @CurrentUser
  public ResponseResult updateAppointment(@RequestBody @Validated AppointmentBaseForm form) {
    ResponseResult responseResult = appointmentBiz.updateAppointment(form);
    return responseResult;
  }

  /**
   * 修改预约（继续保存）
   *
   * @param appointmentForm 修改预约表单
   * @return ResponseResult
   */
  @ApiOperation(value = "修改预约（继续保存）")
  @PutMapping("/update/continue")
  @CurrentUser
  public ResponseResult editAppointmentContinueSave(
      @RequestBody @Validated AppointmentBaseForm appointmentForm) {
    ResponseResult responseResult = this.appointmentBiz.continueUpdateAppointment(appointmentForm);
    return responseResult;
  }

  /**
   * 修改预约状态
   *
   * @param id 预约id
   * @param form 预约表单
   * @return ResponseResult
   */
  @ApiOperation(value = "修改预约状态")
  @PutMapping("/update/appoint_status/{id}/{appointStatus}")
  @CurrentUser
  public ResponseResult updateAppointStatus(
      @PathVariable("id") Integer id, @RequestBody @Validated AppointStatusForm form) {
    ResponseResult responseResult = appointmentBiz.updateAppointStatus(id, form.getAppointStatus(), form.getRemarks());
    return responseResult;
  }

  /**
   * 取消预约/删除预约（逻辑删除）
   *
   * @param id 预约id
   * @param form 取消预约原因表单
   * @return ResponseResult
   */
  @ApiOperation(value = "取消预约/删除预约（逻辑删除）")
  @DeleteMapping("/delete/appoint/{id}")
  @CurrentUser
  public ResponseResult AppointmentCancel(
      @PathVariable("id") Integer id, @Validated AppointmentCancelCauseForm form) {
    ResponseResult responseResult = appointmentBiz.appointmentCancel(id, form);
    return responseResult;
  }

  /**
   * 确认预约（就诊画面用）
   *
   * @param id 预约id
   * @return ResponseResult
   */
  @ApiOperation(value = "确认预约（就诊画面用）")
  @PutMapping("/confirm/{id}")
  @CurrentUser
  public ResponseResult appointConfirm(@PathVariable("id") Integer id) {
    ResponseResult responseResult = appointmentBiz.confirmAppointment(id);
    return responseResult;
  }

  /**
   * 根据条件查询患者预约（患者档案-预约信息；查看详细-预约信息）用
   * @param query 查询条件
   * @return 预约列表
   */
  @ApiOperation(value = "根据条件查询患者预约（患者档案-预约信息；查看详细-预约信息）用")
  @GetMapping("/find/patient/record")
  public ResponseResult findAppointPatientRecord(@Validated AppointPatientRecordQuery query) {
    return appointmentBiz.findAppointPatientRecord(query);
  }

  /**
   * 根据id查询预约
   *
   * @param id 预约id
   * @return
   */
  @ApiOperation(value = "根据id查询预约")
  @GetMapping("/find/{id}")
  public ResponseResult findAppointById(@PathVariable("id") Integer id) {
    AppointmentVo appointmentVo = appointmentBiz.findAppointmentById(id);
    return ResponseUtil.success(appointmentVo);
  }

  /**
   * 根据条件查询预约可视图（患者维度）
   *
   * @param query 查询参数
   * @return
   */
  @ApiOperation(value = "根据条件查询患者维度预约可视图(按医生id、时间段查询)")
  @PostMapping("/find/patient/dimension")
  public ResponseResult findAppointmentPatientDimensionByDate(
      @RequestBody @Validated PatientDimensionByDayQuery query) {

    List<AppointmentDimensionVo> appointmentDimensionVos =
        appointmentBiz.findAppointmentPatientDimensionByExample(query);
    // 分页
    PageUtil pageUtil = new PageUtil(query.getPageNum(),query.getPageSize());
    Page paging = pageUtil.getPaging(appointmentDimensionVos);
    return ResponseUtil.success(paging);
  }

  /**
   * 根据排班开始结束日期/门诊id/医生id查询医生维度预约可视图（医生维度）
   *
   * @param query 查询参数
   * @return
   */
  @ApiOperation(value = "根据排班开始结束日期/门诊id/医生id查询医生维度预约可视图")
  @PostMapping("/find/dentist/dimension")
  public ResponseResult findAppointmentDentistDimensionByExample(
      @RequestBody PatientDimensionByDayQuery query) {
    return appointmentBiz.findAppointmentDentistDimensionByExample(query);
  }

  /**
   * 根据条件查询预约列表（预约列表可视图用）
   *
   * @param query 查询条件
   * @return 预约列表
   */
  @ApiOperation(value = "根据条件查询预约列表（预约列表可视图用）")
  @PostMapping("/find/list")
  public ResponseResult findAppointmentListByExample(
      @RequestBody @Validated AppointListQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<AppointmentListItemVo> appointmentList =
        appointmentBiz.findAppointmentListByExample(query);
    PageInfo pageInfo = new PageInfo(appointmentList);

    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询预约未到列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询预约未到列表(可分页)")
  @PostMapping("/list/current")
  public ResponseResult findUnComingAppointmentList(
      @RequestBody @Validated AppointmentCurrentListQuery queryForm) {
    PageInfo<AppointmentUnDonePatientInfoVO> resultList =
        appointmentBiz.findUnComingAppointmentList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 导出预约列表
   * @param response  HttpServletResponse
   * @param query   参数封装
   * @return ResponseResult
   * @throws IOException
   */
  @ApiOperation(value = "导出预约列表")
  @PostMapping("/export/list")
  @CurrentUser
  public ResponseResult exportAppointListToExcel(
          HttpServletResponse response,
          @RequestBody @Validated AppointListExportQuery query) throws IOException {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(),query.getPageSize());
    }
    appointmentBiz.exportAppointListToExcel(response,query);
    return ResponseUtil.success();
  }

}
