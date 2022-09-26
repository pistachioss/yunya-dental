package com.yunya.modules.appointment.biz.app;

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

@Service
public class ReservationCodeBiz extends BaseBiz<ReservationCodeMapper, ReservationCode> {

  public boolean find(ReservationCodeQuery query) {
    Example example = new Example(ReservationCode.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("code", query.getCode());
    int count = mapper.selectCountByExample(example);
    if (count > 0) {
      return true;
    } else {
      return false;
    }
  }
}
