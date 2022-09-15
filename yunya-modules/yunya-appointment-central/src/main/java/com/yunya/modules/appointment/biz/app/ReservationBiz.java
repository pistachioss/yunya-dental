package com.yunya.modules.appointment.biz.app;

import com.yunya.feign.appointment.domain.model.ReservationModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.OnlineAppointment;
import com.yunya.models.appointment.Reservation;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.ReservationMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReservationBiz extends BaseBiz<ReservationMapper, Reservation> {

    public ResponseResult add(ReservationModel model) {
        Reservation build = EntityUtils.build(model, Reservation.class);
        build.setCrtName(BaseContextHandler.getUsername());
        int status = mapper.insertSelective(build);
        if (status <= 0) {
            ResponseUtil.fail(AppointmentError.APPOINTMENT_FAIL.getCode(),AppointmentError.APPOINTMENT_FAIL.getMessage(),null);
        }
        return ResponseUtil.success(build.getId());
    }

    public ResponseResult update(ReservationModel model) {
        Reservation build1 = mapper.selectByPrimaryKey(model.getId());
        if (build1 == null){
            throw new ClientServiceException("修改的数据不存在！", OperationCodeConstants.DATA_NOT_EXIST);
        }
        Reservation build = EntityUtils.build(model, Reservation.class);
        build.setUpdName(BaseContextHandler.getUsername());
        build.setUpdTime(new Date(System.currentTimeMillis()));
        int status = mapper.updateByPrimaryKeySelective(build);
        return ResponseUtil.success(status);
    }

    public ResponseResult delete(Integer id) {
        int status = mapper.deleteByPrimaryKey(id);
        return ResponseUtil.success(status);
    }

    public ResponseResult list() {
        return ResponseUtil.success(mapper.selectAll());
    }
}
