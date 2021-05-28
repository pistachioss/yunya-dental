package com.yunya.feign.appointment;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.AppointmentForMonthForm;
import com.yunya.feign.appointment.domain.form.OnlineAppointItemSettingForm;
import com.yunya.feign.appointment.domain.query.AppAppointmentInfoQuery;
import com.yunya.feign.appointment.domain.query.AppointItemQuery;
import com.yunya.feign.appointment.domain.query.AppointmentCurrentListQuery;
import com.yunya.feign.appointment.factory.RemoteAppointmentFeignBackFactory;
import com.yunya.feign.appointment.vo.AppointmentItemEnableModelVo;
import com.yunya.feign.appointment.vo.AppointmentItemVo;
import com.yunya.feign.appointment.vo.AppointmentUnDonePatientInfoVO;
import com.yunya.feign.appointment.vo.AppointmentVo;
import com.yunya.feign.appointment.vo.NextAppointsVo;
import com.yunya.feign.treatment.domain.vo.TreatmentInfoForMonthVO;
import com.yunya.feign.wechat.domain.model.WxAppointConfirmModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.appointment.AppointType;
import com.yunya.models.appointment.Appointment;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 预约系统服务接口调用
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 16:58
 * @update yunya-lihuibin 2020-07-31 新建
 */
@FeignClient(
    name = YunyaServiceNameConstants.YUNYA_CLINIC_APPOINTMENT,
    fallbackFactory = RemoteAppointmentFeignBackFactory.class)
public interface RemoteAppointmentFeign {

  /**
   * 根据条件查询门诊可预约项目
   *
   * @param form 条件查询
   * @return
   * @description 查询公司端、门诊端的预约列表，Mock两端数据，返回vo对象列表
   */
  @RequestMapping(value = "/api/list", method = RequestMethod.POST)
  PageInfo<AppointmentItemVo> findAppItemList(@RequestBody AppointItemQuery form);

  /**
   * 预约搜索
   *
   * @param baseQueryForm 查询条件
   * @return
   */
  @RequestMapping(value = "/api/search", method = RequestMethod.POST)
  List<AppointmentItemVo> searchAppItem(@RequestBody AppointItemQuery baseQueryForm);

  /**
   * 获取门诊可预约的项目列表
   *
   * @param orgId 门诊id
   * @return
   */
  @RequestMapping(value = "/api/available/{orgId}", method = RequestMethod.GET)
  List<AppointmentItemEnableModelVo> findAvailableAppItem(
      @PathVariable(value = "orgId") String orgId);

  /**
   * 根据id查询预约项目种类
   *
   * @param id 预约项目id
   * @return
   */
  @RequestMapping(value = "/api/select/{id}", method = RequestMethod.GET)
  AppointType selectAppointTypeById(@PathVariable(value = "id") Integer id);

  /**
   * 修改预约信息
   *
   * @param appointment 修改表单
   * @return
   */
  @RequestMapping(value = "/api/appoint/update", method = RequestMethod.POST)
  void updateAppointment(@RequestBody Appointment appointment);

  /**
   * 通过id查询预约信息
   *
   * @param id 患者预约id
   * @return
   */
  @RequestMapping(value = "/api/appoint/find/{id}", method = RequestMethod.GET)
  Appointment findAppointmentById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询预约列表
   *
   * @param patientId 患者id
   * @return List<Appointment>
   */
  @RequestMapping(value = "/api/appoint/find/patients/{patientId}", method = RequestMethod.GET)
  List<Appointment> findAppointmentByPatientId(
      @PathVariable(value = "patientId") Integer patientId);

  /**
   * 根据条件查询预约列表
   *
   * @param query 查询条件封装
   * @return List<Appointment>
   */
  @RequestMapping(value = "/api/appoint/app/list", method = RequestMethod.POST)
  PageInfo<Appointment> findAppointmentList(@RequestBody AppAppointmentInfoQuery query);

  /**
   * 计算预约未到列表数量
   *
   * @param queryForm 查询条件
   * @return
   */
  @RequestMapping(value = "/api/appoint/count", method = RequestMethod.POST)
  Integer countAppointNotArrived(@RequestBody AppointmentCurrentListQuery queryForm);

  /**
   * 计算后续指定患者的预约数量
   * @param patientId 患者ID
   * @return 返回预约数量
   */
  @RequestMapping(value = "/api/appoint/count/{patientId}", method = RequestMethod.GET)
  Integer countNextAppoint(@PathVariable(value = "patientId") Integer patientId);

  /**
   * 计算后续指定患者的预约数量
   * @param patientIds 患者ID
   * @return 返回预约数量
   */
  @RequestMapping(value = "/api/appoint/count/patientIds", method = RequestMethod.POST)
  List<NextAppointsVo> countNextAppoints(@RequestBody List<Integer> patientIds);

  /**
   * 根据预约ID查询预约
   * @param appointIds 预约ID集合
   * @return 预约ID集合
   */
  @RequestMapping(value = "/api/appoint/ids", method = RequestMethod.POST)
  List<Appointment> findAppointmentListByIds(@RequestBody List<Integer> appointIds);

  /**
   * 根据条件查询预约未到患者信息列表
   * @param queryForm 查询条件
   * @return list
   */
  @RequestMapping(value = "/api/appoint/unregister/list", method = RequestMethod.POST)
  PageInfo<AppointmentUnDonePatientInfoVO> findUnComingAppointmentList(@RequestBody AppointmentCurrentListQuery queryForm);

  /**
   * 查询指定时间段内每个医生每天预约人数
   *
   * @param form 查询条件表单
   * @return 返回实体列表
   */
  @RequestMapping(value = "/api/appoint/app/count", method = RequestMethod.POST)
  List<TreatmentInfoForMonthVO> appointmentForMonth(@RequestBody AppointmentForMonthForm form);

  /**
   * 根据预约ID查询预约详细信息
   * @param id 预约ID
   * @return 返回预约信息实体
   */
  @RequestMapping(value = "/api/appoint/detail/{id}", method = RequestMethod.GET)
  AppointmentVo findAppointmentDetailById(@PathVariable(value = "id") Integer id);

  /**
   * 微信用户确认预约
   * @param model 预约
   */
  @RequestMapping(value = "/api/wx/appoint/confirm", method = RequestMethod.POST)
  ResponseResult confirmWxAppoint(@RequestBody WxAppointConfirmModel model);

  /**
   * 新增、更新线上预约设置
   * @param form
   */
  @RequestMapping(value = "api/online/appoint/",method = RequestMethod.POST)
  ResponseResult addOrUpdateOnlineAppointItem(@RequestBody @Validated OnlineAppointItemSettingForm form);
}
