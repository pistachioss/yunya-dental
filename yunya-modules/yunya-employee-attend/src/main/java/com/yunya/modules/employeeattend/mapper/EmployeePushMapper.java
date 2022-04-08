package com.yunya.modules.employeeattend.mapper;

import com.yunya.models.employee_attend.EmployeePush;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface EmployeePushMapper extends Mapper<EmployeePush> {

    List<EmployeePush> selectByEmployeeIds(List<Integer> list);

    EmployeePush selectOneByRegId(@Param("regId") String regId);
}