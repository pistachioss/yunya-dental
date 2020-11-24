package com.yunya.modules.employeeattend.mapper;

import com.yunya.models.employee_attend.LeaveSchedule;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface LeaveScheduleMapper extends Mapper<LeaveSchedule> {
    int batchInsert(List<LeaveSchedule> list);

    int selectNum(List<LeaveSchedule>list);
}