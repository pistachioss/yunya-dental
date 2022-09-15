package com.yunya.modules.appointment.biz.app;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.AppointItem;
import com.yunya.models.appointment.ReservationResource;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.ReservationResourceMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReservationResourceBiz extends BaseBiz<ReservationResourceMapper, ReservationResource> {

    public ResponseResult add(String sourceName) {
        ReservationResource model = new ReservationResource();
        model.setSourceName(sourceName);
        model.setCrtName(BaseContextHandler.getUsername());
        int status = mapper.insertSelective(model);
        if (status <= 0) {
            ResponseUtil.fail(AppointmentError.APPOINTMENT_FAIL.getCode(),AppointmentError.APPOINTMENT_FAIL.getMessage(),null);
        }
        return ResponseUtil.success(model.getId());
    }

    public ResponseResult update(Integer id, String sourceName) {
        ReservationResource model = mapper.selectByPrimaryKey(id);
        if (model == null){
            throw new ClientServiceException("修改的数据不存在！", OperationCodeConstants.DATA_NOT_EXIST);
        }
        model.setSourceName(sourceName);
        model.setUpdName(BaseContextHandler.getUsername());
        model.setUpdTime(new Date(System.currentTimeMillis()));
        int status = mapper.updateByPrimaryKeySelective(model);
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
