package com.yunya.modules.appointment.controller.web;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.AppointStatusForm;
import com.yunya.feign.appointment.domain.form.AppointmentBaseForm;
import com.yunya.feign.appointment.domain.form.AppointmentCancelCauseForm;
import com.yunya.feign.appointment.domain.model.AppointmentBaseModel;
import com.yunya.feign.appointment.domain.query.*;
import com.yunya.feign.appointment.vo.*;
import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.sms.model.AppointmentSmsSendRecordModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.modules.appointment.biz.web.AppointmentBiz;
import com.yunya.modules.appointment.biz.web.ClinicAppointSettingBiz;
import com.yunya.modules.appointment.util.pageUtil.PageUtil;
import com.yunya.modules.appointment.util.pageUtil.model.Page;
import io.swagger.annotations.*;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
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

  private final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

  /** 预约服务 */
  @Autowired private AppointmentBiz appointmentBiz;

  @Autowired EmployeeAttendServiceFeign employeeAttendServiceFeign;

  @Autowired
  private ClinicAppointSettingBiz clinicAppointSettingBiz;

  /**
   * 添加预约（有冲突检测）
   *
   * @param form 预约数据
   * @return 预约冲突信息
   * @throws ParseException 异常
   */
  @ApiOperation(value = "添加预约（有冲突检测）")
  @PostMapping("/add")
  @CurrentUser
  @RepeatSubmit
  public ResponseResult addAppointment(@RequestBody @Validated AppointmentBaseModel form)
      throws ParseException {
    return appointmentBiz.addAppointment(form);
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
  @RepeatSubmit
  public ResponseResult continueAddAppointment(
      @RequestBody @Validated AppointmentBaseModel appointmentForm) {
    return appointmentBiz.continueAddAppointment(appointmentForm);
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
  @RepeatSubmit
  public ResponseResult updateAppointment(@RequestBody @Validated AppointmentBaseForm form) {
    return appointmentBiz.updateAppointment(form);
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
  @RepeatSubmit
  public ResponseResult editAppointmentContinueSave(
      @RequestBody @Validated AppointmentBaseForm appointmentForm) {
    return this.appointmentBiz.continueUpdateAppointment(appointmentForm);
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
  @RepeatSubmit
  public ResponseResult updateAppointStatus(
      @PathVariable("id") Integer id, @RequestBody @Validated AppointStatusForm form) {
    return appointmentBiz.updateAppointStatus(id, form.getAppointStatus(), form.getRemarks());
  }

  /**
   * 取消预约/删除预约（逻辑删除）
   * @param form 取消预约原因
   * @return ResponseResult
   */
  @ApiOperation(value = "取消预约/删除预约（逻辑删除）")
  @PostMapping("/delete/appoint")
  @CurrentUser
  @RepeatSubmit
  public ResponseResult AppointmentCancel(
      @RequestBody @Validated AppointmentCancelCauseForm form) {
    return appointmentBiz.appointmentCancel(form.getId(), form.getCause());
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
  @RepeatSubmit
  @ApiImplicitParams({
          @ApiImplicitParam(name = "id", value = "预约ID", dataTypeClass = Integer.class),
          @ApiImplicitParam(name = "flag", value = "确认状态,true-已确认；false-未确认", dataTypeClass = java.lang.Boolean.class)
  })
  public ResponseResult appointConfirm(@PathVariable("id") Integer id, @RequestParam("flag") Boolean flag) {
    return appointmentBiz.confirmAppointment(id,flag);
  }

  /**
   * 根据条件查询患者预约（患者档案-预约信息；查看详细-预约信息）用
   * @param query 查询条件
   * @return 预约列表
   */
  @ApiOperation(value = "根据条件查询患者预约（患者档案-预约信息；查看详细-预约信息）用")
  @ApiResponse(code = 0, message = "success", response = ResponseResult.class)
  @PostMapping("/find/patient/record")
  public ResponseResult<PageInfo<AppointPatientRecordVo>> findAppointPatientRecord(@RequestBody @Validated AppointPatientRecordQuery query) {
    return appointmentBiz.findAppointPatientRecord(query);
  }

  /**
   * 根据id查询预约
   *
   * @param id 预约id
   * @return 预约信息
   */
  @ApiOperation(value = "根据id查询预约")
  @GetMapping("/find/{id}")
  public ResponseResult<AppointmentVo> findAppointById(@PathVariable("id") Integer id) {
    AppointmentVo appointmentVo = appointmentBiz.findAppointmentById(id);
    return ResponseUtil.success(appointmentVo);
  }

  /**
   * 根据条件查询预约可视图（患者维度）
   *
   * @param query 查询参数
   * @return 患者维度列表
   */
  @ApiOperation(value = "根据条件查询患者维度预约可视图(按医生id、时间段查询)")
  @PostMapping("/find/patient/dimension")
  public ResponseResult<Page<AppointmentDimensionVo>> findAppointmentPatientDimensionByDate(
      @RequestBody @Validated PatientDimensionByDayQuery query) {
    List<AppointmentDimensionVo> appointmentDimensionVos =
        appointmentBiz.findAppointmentPatientDimensionByExample(query);
    // 分页
    PageUtil pageUtil = new PageUtil(query.getPageNum(),query.getPageSize());
    Page<AppointmentDimensionVo> paging = pageUtil.getPaging(appointmentDimensionVos);
    return ResponseUtil.success(paging);
  }

  /**
   * 根据条件查询预约可视图（患者维度）
   *
   * @param query 查询参数
   * @return 患者维度列表
   */
  @ApiOperation(value = "根据条件查询患者维度预约可视图(按医生id、时间段查询) 多个医生")
  @PostMapping("/find/patient/dimensionmore")
  public ResponseResult<Page<AppointmentDimensionsVO>> findAppointmentPatientDimensionByDateMore(
          @RequestBody @Validated PatientDimensionByDayQuery query) {
    List<AppointmentDimensionsVO> appointmentDimensionVos =
            appointmentBiz.findAppointmentPatientDimensionByExampleMore(query);
    // 分页
    PageUtil pageUtil = new PageUtil(query.getPageNum(),query.getPageSize());
    Page<AppointmentDimensionsVO> paging = pageUtil.getPaging(appointmentDimensionVos);
    return ResponseUtil.success(paging);
  }

  /**
   * 根据排班开始结束日期/门诊id/医生id查询医生维度预约可视图（医生维度）
   *
   * @param query 查询参数
   * @return 医生维度列表
   */
  @ApiOperation(value = "根据排班开始结束日期/门诊id/医生id查询医生维度预约可视图")
  @PostMapping("/find/dentist/dimension")
  @CurrentUser
  public ResponseResult<Page<AppointmentDimensionVo>> findAppointmentDentistDimensionByExample(
      @RequestBody PatientDimensionByDayQuery query){
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
  public ResponseResult<PageInfo<AppointmentListItemVo>> findAppointmentListByExample(
      @RequestBody @Validated AppointListQuery query) {
    PageInfo<AppointmentListItemVo> pageInfo =
        appointmentBiz.findAppointmentListByExample(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询预约未到列表
   *
   * @param queryForm 查询条件
   * @return 预约未到列表
   */
  @ApiOperation("根据条件查询预约未到列表(可分页)")
  @PostMapping("/list/current")
  public ResponseResult<PageInfo<AppointmentUnDonePatientInfoVO>> findUnComingAppointmentList(
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
   * @throws IOException 异常
   */
  @ApiOperation(value = "导出预约列表")
  @PostMapping("/export/list")
  @CurrentUser
  @RepeatSubmit
  public ResponseResult exportAppointListToExcel(
          HttpServletResponse response,
          @RequestBody @Validated AppointListExportQuery query) throws IOException {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(),query.getPageSize());
    }
    appointmentBiz.exportAppointListToExcel(response,query);
    return ResponseUtil.success();
  }

  /**
   * 发送预约短信
   *
   * @param templateId 短信模板id
   * @param models 短信预约提醒列表
   * @return
   */
  @Deprecated
  @ApiOperation(value = "发送预约短信")
  @PostMapping("/sendAppointmentBatchSms/{templateId}")
  @CurrentUser
  @RepeatSubmit
  public ResponseResult<T> sendAppointmentBatchSms(@PathVariable(value = "templateId") @NotNull Integer templateId, @RequestBody @Validated List<AppointmentSmsSendRecordModel> models) {
    return appointmentBiz.sendAppointmentBatchSms(templateId, models);
  }

  /**
   * 根据条件查询取消预约明细表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("根据条件查询取消预约明细表")
  @PostMapping(value = "/cancelAppointment/list", name = "公司端-运营报表-患者报表-取消预约明细表")
  public ResponseResult<PageInfo<CancelAppointmentVO>> cancelAppointmentList(@RequestBody @Validated CancelAppointmentQuery query) {
    PageInfo<CancelAppointmentVO> arrears = appointmentBiz.cancelAppointmentList(query);
    if (StringHelper.isNotNull(arrears)) {
      return ResponseUtil.success(arrears);
    }
    return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL, "暂无相关数据", null);
  }

  /**
   * 根据条件导出取消预约明细表
   *
   * @param response 响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("根据条件导出取消预约明细表")
  @PostMapping(value = "/cancelAppointment/export", name = "公司端-运营报表-患者报表-取消预约明细表导出")
  public ResponseResult<T> cancelAppointmentExport(
          HttpServletResponse response, @RequestBody @Validated CancelAppointmentQuery query)
          throws IOException {
    appointmentBiz.cancelAppointmentExport(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 获取门诊营业时间
   * @param orgId 门诊ID
   * @return 返回营业时间
   */
  @ApiOperation("获取门诊营业时间")
  @GetMapping("/{orgId}/businessHours")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "orgId",value = "门诊ID", required = true, dataTypeClass = Integer.class, defaultValue = "26")
  })
  public ResponseResult<ClinicBusinessHoursVo> orgBusinessHours(@PathVariable("orgId") @NotNull(message = "请选择门诊") Integer orgId) {
    return clinicAppointSettingBiz.orgBusinessHours(orgId);
  }

  /**
   * 查询下次预约信息
   *
   * @param patientId
   * @return
   */
  @ApiOperation("查询下次预约信息")
  @GetMapping("/next/{patientId}")
  public ResponseResult<NextAppointmentVO> findNextAppointment(@PathVariable(value = "patientId") Integer patientId) {
    NextAppointmentVO vo = appointmentBiz.findNextAppointment(patientId);
    return ResponseUtil.success(vo);
  }
}
