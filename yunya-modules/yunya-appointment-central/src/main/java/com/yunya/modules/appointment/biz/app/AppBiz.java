package com.yunya.modules.appointment.biz.app;

import com.yunya.feign.appointment.domain.form.AppointmentForMonthForm;
import com.yunya.feign.appointment.vo.AppointmentForMonthVo;
import com.yunya.feign.treatment.domain.query.TreatmentInfoForMonthForm;
import com.yunya.feign.treatment.domain.vo.TreatmentInfoForMonthVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.appointment.Appointment;
import com.yunya.modules.appointment.mapper.AppointmentMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: app端相关业务
 * @author: LHB
 * @create: 2020-11-23 13:11
 **/
@Service
public class AppBiz extends BaseBiz<AppointmentMapper, Appointment> {


    /**
     * 查询指定时间段内每个医生每天预约人数
     *
     * @param form 查询条件表单
     * @return 返回实体列表
     */
    public List<TreatmentInfoForMonthVO> appointmentForMonth(AppointmentForMonthForm form) {
        Integer dentistId = form.getDentistId();
        Date startDate = form.getStartDate();
        Date endDate = form.getEndDate();
        Integer orgId = form.getOrgId();
        return mapper.appointmentForMonth(dentistId, startDate, endDate,orgId);
    }
}
