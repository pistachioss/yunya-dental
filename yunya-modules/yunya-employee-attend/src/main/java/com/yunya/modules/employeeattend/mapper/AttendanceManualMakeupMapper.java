package com.yunya.modules.employeeattend.mapper;

import com.yunya.models.employee_attend.AttendanceManualMakeup;
import tk.mybatis.mapper.common.Mapper;

public interface AttendanceManualMakeupMapper extends Mapper<AttendanceManualMakeup> {

    @Override
    int insert(AttendanceManualMakeup attendanceManualMakeup);
}