package com.yunya.modules.employeeattend.mapper;

import com.yunya.models.employee_attend.VacationSet;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface VacationSetMapper extends Mapper<VacationSet> {
    List<VacationSet> selectList(VacationSet vacationSet);

    List<VacationSet> selectListPC(VacationSet vacationSet);

}