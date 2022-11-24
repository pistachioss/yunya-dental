package com.yunya.modules.appointment.biz.app;

import com.google.common.base.Strings;
import com.yunya.feign.appointment.domain.query.ReservationCodeQuery;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.ReservationCode;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.ReservationCodeMapper;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class ReservationCodeBiz extends BaseBiz<ReservationCodeMapper, ReservationCode> {

  public boolean find(ReservationCodeQuery query) {
    if ( Strings.isNullOrEmpty(query.getCode())) {
      return false;
    }
    Example example = new Example(ReservationCode.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("code", query.getCode());
    List<ReservationCode> reservationCodeList = mapper.selectByExample(example);
    if (reservationCodeList.size() == 1) {
      ReservationCode reservationCode = reservationCodeList.get(0);
      if (reservationCode.getCodeStatus() > 1) {
        return false;
      }
      if (reservationCode.getCodeStartDate().after(new Date())) {
        return false;
      }
      if (reservationCode.getCodeEndDate().before(new Date())) {
        return false;
      }
      return true;
    } else {
      return false;
    }
  }
}
