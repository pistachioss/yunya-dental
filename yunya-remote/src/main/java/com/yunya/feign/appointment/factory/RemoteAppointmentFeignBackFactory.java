package com.yunya.feign.appointment.factory;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.appointment.domain.form.AppointmentForMonthForm;
import com.yunya.feign.appointment.domain.form.OnlineAppointItemSettingForm;
import com.yunya.feign.appointment.domain.query.AppAppointmentInfoQuery;
import com.yunya.feign.appointment.domain.query.AppointItemQuery;
import com.yunya.feign.appointment.domain.query.AppointmentCurrentListQuery;
import com.yunya.feign.appointment.vo.*;
import com.yunya.feign.treatment.domain.vo.TreatmentInfoForMonthVO;
import com.yunya.feign.wechat.domain.model.WxAppointConfirmModel;
import com.yunya.feign.wechat.domain.vo.WxAppointConfirmPushVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.appointment.AppointType;
import com.yunya.models.appointment.Appointment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 系统服务调用降级处理
 * @author yunya-lihuibin
 * @create 2020-07-31 17:00
 * @update yunya-lihuibin    2020-07-31
 */
@Slf4j
@Component
public class RemoteAppointmentFeignBackFactory implements RemoteAppointmentFeign {
    @Override
    public PageInfo<AppointmentItemVo> findAppItemList(AppointItemQuery form) {
        return null;
    }

    @Override
    public List<AppointmentItemVo> searchAppItem(AppointItemQuery baseQueryForm) {
        return null;
    }

    @Override
    public List<AppointmentItemEnableModelVo> findAvailableAppItem(String compClinId) {
        return null;
    }

    @Override
    public AppointType selectAppointTypeById(Integer id) {
        return null;
    }

    @Override
    public void updateAppointment(Appointment appointment) {

    }

    @Override
    public Appointment findAppointmentById(Integer id) {
        return null;
    }

    @Override
    public List<Appointment> findAppointmentByPatientId(Integer patientId) {
        return null;
    }

    @Override
    public PageInfo<Appointment> findAppointmentList(AppAppointmentInfoQuery query) {
        return null;
    }

    @Override
    public Integer countAppointNotArrived(AppointmentCurrentListQuery queryForm) {
        return null;
    }

    @Override
    public Integer countNextAppoint(Integer patientId) {
        return null;
    }

    @Override
    public List<NextAppointsVo> countNextAppoints(List<Integer> patientIds) {
        return null;
    }

    @Override
    public List<Appointment> findAppointmentListByIds(List<Integer> appointIds) {
        return null;
    }

    @Override
    public PageInfo<AppointmentUnDonePatientInfoVO> findUnComingAppointmentList(AppointmentCurrentListQuery queryForm) {
        return null;
    }

    @Override
    public List<TreatmentInfoForMonthVO> appointmentForMonth(AppointmentForMonthForm form) {
        return null;
    }

    @Override
    public AppointmentVo findAppointmentDetailById(Integer id) {
        return null;
    }

    @Override
    public ResponseResult confirmWxAppoint(WxAppointConfirmModel model) {
        return null;
    }

    @Override
    public ResponseResult addOrUpdateOnlineAppointItem(OnlineAppointItemSettingForm form) {
        return null;
    }

}
