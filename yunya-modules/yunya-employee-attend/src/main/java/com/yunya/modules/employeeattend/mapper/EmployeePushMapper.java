package com.yunya.modules.employeeattend.mapper;

import com.yunya.models.employee_attend.EmployeePush;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

public interface EmployeePushMapper extends Mapper<EmployeePush> {

    List<EmployeePush> selectByEmployeeIds(List<Integer> list);
}