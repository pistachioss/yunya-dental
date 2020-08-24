package com.yunya.feign.appointment;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.query.AppointItemQuery;
import com.yunya.feign.appointment.factory.RemoteAppointmentFeignBackFactory;
import com.yunya.feign.appointment.vo.AppointmentItemEnableModelVo;
import com.yunya.feign.appointment.vo.AppointmentItemVo;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.appointment.AppointType;
import com.yunya.models.appointment.Appointment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约系统服务接口调用
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 16:58
 * @update yunya-lihuibin    2020-07-31    新建
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
    @RequestMapping(value = "/api/search",method = RequestMethod.POST)
    List<AppointmentItemVo> searchAppItem(@RequestBody AppointItemQuery baseQueryForm);

    /**
     * 获取门诊可预约的项目列表
     *
     * @param orgId 门诊id
     * @return
     */
    @RequestMapping(value = "/api/available/{orgId}",method = RequestMethod.GET)
    List<AppointmentItemEnableModelVo> findAvailableAppItem(@PathVariable(value = "orgId") String orgId);

    /**
     * 根据id查询预约项目种类
     * @param id 预约项目id
     * @return
     */
    @RequestMapping(value = "/api/select/{id}",method = RequestMethod.GET)
    AppointType selectAppointTypeById(@PathVariable(value = "id") Integer id);

    /**
     * 修改预约信息
     * @param appointment  修改表单
     * @return
     */
    @RequestMapping(value = "/api/appoint/update", method = RequestMethod.POST)
    void updateAppointment(@RequestBody Appointment appointment);

    /**
     * 通过id查询预约信息
     * @param id  患者预约id
     * @return
     */
    @RequestMapping(value = "/api/appoint/find/{id}", method = RequestMethod.GET)
    Appointment findAppointmentById(@PathVariable(value = "id") Integer id);

    /**
     * 根据条件查询预约列表
     * @param patientId 患者id
     * @return  List<Appointment>
     */
    @RequestMapping(value = "/appoint/find/patient/list/{patientId}", method = RequestMethod.GET)
    List<Appointment> findAppointmentByPatientId(@PathVariable(value = "patientId") Integer patientId);


}
