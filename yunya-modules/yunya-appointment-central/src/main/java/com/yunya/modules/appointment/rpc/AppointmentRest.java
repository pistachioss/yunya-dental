package com.yunya.modules.appointment.rpc;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.AppointmentForMonthForm;
import com.yunya.feign.appointment.domain.query.AppAppointmentInfoQuery;
import com.yunya.feign.appointment.domain.query.AppointItemQuery;
import com.yunya.feign.appointment.domain.query.AppointmentCurrentListQuery;
import com.yunya.feign.appointment.vo.AppointmentItemEnableModelVo;
import com.yunya.feign.appointment.vo.AppointmentItemVo;
import com.yunya.feign.appointment.vo.AppointmentUnDonePatientInfoVO;
import com.yunya.feign.appointment.vo.AppointmentVo;
import com.yunya.feign.appointment.vo.NextAppointsVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientEventVO;
import com.yunya.feign.treatment.domain.vo.TreatmentInfoForMonthVO;
import com.yunya.feign.wechat.domain.model.WxAppointConfirmModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.AppointType;
import com.yunya.models.appointment.Appointment;
import com.yunya.modules.appointment.biz.app.AppBiz;
import com.yunya.modules.appointment.biz.web.AppointItemBiz;
import com.yunya.modules.appointment.biz.web.AppointTypeBiz;
import com.yunya.modules.appointment.biz.web.AppointmentBiz;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 预约中心服务调用API
 *
 * @author yunya-lihuibin
 * @create 2020-08-11 12:52
 * @update yunya-lihuibin 2020-08-11 新建
 */
@Api(tags = "预约中心服务调用API")
@RestController
@RequestMapping("api/")
@Slf4j
public class AppointmentRest {

  @Autowired private AppointmentBiz appointmentBiz;

  @Autowired private AppointItemBiz baseBiz;

  @Autowired private AppointTypeBiz appointTypeBiz;
  @Autowired
  private AppBiz appBiz;

  /**
   * 根据条件查询门诊可预约项目
   *
   * @param form
   * @return
   * @description 查询公司端、门诊端的预约列表，Mock两端数据，返回vo对象列表
   */
  @RequestMapping(value = "/item/list", method = RequestMethod.POST)
  public List<AppointmentItemVo> findAppItemList(@RequestBody AppointItemQuery form) {
    // 查询门诊端的预约列表
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }

    List<AppointmentItemVo> appItemList = baseBiz.findAppItemList(form);
    return appItemList;
  }

  /**
   * 预约搜索
   *
   * @param baseQueryForm 查询条件
   * @return
   */
  @RequestMapping(value = "/item/search", method = RequestMethod.POST)
  public List<AppointmentItemVo> searchAppItem(@RequestBody AppointItemQuery baseQueryForm) {
    return baseBiz.findAppointItemByExample(baseQueryForm);
  }

  /**
   * 获取门诊可预约的项目列表
   *
   * @param orgId 门诊id
   * @return
   */
  @RequestMapping(value = "/item/available/{orgId}", method = RequestMethod.GET)
  public List<AppointmentItemEnableModelVo> findAvailableAppItem(
      @PathVariable(value = "orgId") Integer orgId) {
    return baseBiz.findAvailableAppItemList(orgId);
  }

  /**
   * 根据id查询预约项目种类
   *
   * @param id 预约项目id
   * @return
   */
  @RequestMapping(value = "/item/select/{id}", method = RequestMethod.GET)
  public AppointType selectAppointTypeById(@PathVariable(value = "id") Integer id) {
    AppointType appointType = appointTypeBiz.selectAppointTypeById(id);
    return appointType;
  }

  /**
   * 修改预约信息
   *
   * @param appointment 修改表单
   * @return
   */
  @RequestMapping(value = "/appoint/update", method = RequestMethod.POST)
  public void updateAppointment(@RequestBody Appointment appointment) {
    appointmentBiz.updateSelectiveById(appointment);
  }

  /**
   * 通过id查询预约信息
   *
   * @param id 预约id
   * @return
   */
  @RequestMapping(value = "/appoint/find/{id}", method = RequestMethod.GET)
  public Appointment findAppointmentById(@PathVariable(value = "id") Integer id) {
    Appointment appointment = appointmentBiz.selectById(id);
    return appointment;
  }

  /**
   * 根据条件查询预约列表
   *
   * @param patientId 患者id
   * @return List<Appointment>
   */
  @RequestMapping(value = "/appoint/find/patients/{patientId}", method = RequestMethod.GET)
  public List<Appointment> findAppointmentByPatientId(
      @PathVariable(value = "patientId") Integer patientId) {
    return appointmentBiz.findAppointmentByPatientId(patientId);
  }

  /**
   * 根据条件查询预约列表
   *
   * @param query 查询条件封装
   * @return List<Appointment>
   */
  @RequestMapping(value = "/appoint/app/list", method = RequestMethod.POST)
  public PageInfo<Appointment> findAppointmentList(@RequestBody AppAppointmentInfoQuery query) {
    return appointmentBiz.findAppointmentList(query);
  }

  /**
   * 计算预约未到列表数量
   *
   * @param queryForm 查询条件
   * @return
   */
  @RequestMapping(value = "/appoint/count", method = RequestMethod.POST)
  public Integer countAppointNotArrived(@RequestBody AppointmentCurrentListQuery queryForm){
    return appointmentBiz.countAppointNotArrived(queryForm);
  }

  /**
   * 计算后续指定患者的预约数量
   * @param patientId 患者ID
   * @return 返回预约数量
   */
  @RequestMapping(value = "/appoint/count/{patientId}", method = RequestMethod.GET)
  public Integer countNextAppoint(@PathVariable(value = "patientId") Integer patientId){
    return appointmentBiz.countNextAppoint(patientId);
  }

  /**
   * 根据预约ID查询预约
   * @param appointIds 预约ID集合
   * @return 预约ID集合
   */
  @RequestMapping(value = "/appoint/ids", method = RequestMethod.POST)
  public List<Appointment> findAppointmentListByIds(@RequestBody List<Integer> appointIds){
    if (StringHelper.isNotEmpty(appointIds)) {
      return appointmentBiz.appointmentListByIds(appointIds);
    }
    return new ArrayList<>();
  }

  /**
   * 计算后续指定患者的预约数量列表
   * @param patientIds 患者ID
   * @return 返回预约数量
   */
  @RequestMapping(value = "/appoint/count/patientIds", method = RequestMethod.POST)
  List<NextAppointsVo> countNextAppoints(@RequestBody List<Integer> patientIds) {
    return appointmentBiz.countNextAppoints(patientIds);
  }

  /**
   * 根据条件查询预约未到患者信息列表
   * @param queryForm 查询条件
   * @return list
   */
  @RequestMapping(value = "/appoint/unregister/list", method = RequestMethod.POST)
  public PageInfo<AppointmentUnDonePatientInfoVO> findUnComingAppointmentList(@RequestBody AppointmentCurrentListQuery queryForm) {
    PageInfo<AppointmentUnDonePatientInfoVO> unComingAppointmentList = appointmentBiz.findUnComingAppointmentList(queryForm);
    return unComingAppointmentList;
  }

  /**
   * 查询指定时间段内每个医生每天预约人数
   *
   * @param form 查询条件表单
   * @return 返回实体列表
   */
  @RequestMapping(value = "/appoint/app/count", method = RequestMethod.POST)
  public List<TreatmentInfoForMonthVO> appointmentForMonth(@RequestBody AppointmentForMonthForm form) {
    return this.appBiz.appointmentForMonth(form);
  }

  /**
   * 根据预约ID查询预约详细信息
   * @param id 预约ID
   * @return 返回预约信息实体
   */
  @RequestMapping(value = "/appoint/detail/{id}", method = RequestMethod.GET)
  AppointmentVo findAppointmentDetailById(@PathVariable(value = "id") Integer id) {
    return this.appointmentBiz.findAppointmentById(id);
  }

  /**
   * 微信用户确认预约
   * @param model 预约
   */
  @RequestMapping(value = "/wx/appoint/confirm", method = RequestMethod.POST)
  ResponseResult confirmWxAppoint(@RequestBody WxAppointConfirmModel model) {
    return this.appointmentBiz.confirmWxAppointment(model);
  }

  /**
   * 根据患者id查询患者预约轨迹
   *
   * @param patientId
   * @return
   */
  @GetMapping("/appoint/trajectory/{patientId}")
  List<PatientEventVO> findPatientAppointTrajectory(@PathVariable(value = "patientId") Integer patientId) {
    return this.appointmentBiz.findPatientAppointTrajectory(patientId);
  }
}
