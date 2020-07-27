package com.yunya.modules.appointment.mapper;

import com.yunya.models.appointment.AppointmentItemEnableModel;
import com.yunya.models.appointment.AppointmentItemType;
import com.yunya.modules.appointment.form.AppointOrderTypeQueryForm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppointItemMapper {

    List<AppointmentItemEnableModel> AllOrders(Integer compClinId);

    List<AppointmentItemType> getByOrderTypeId(@Param("form") AppointOrderTypeQueryForm form);
}