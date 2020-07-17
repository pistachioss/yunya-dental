package com.yunya.employee.common.mapper;


import com.yunya.clinic.employee.common.entity.ClinicEmployee;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicEmployeeMapper extends Mapper<ClinicEmployee> {

    void insertBatch(@Param("list") List<ClinicEmployee> list);
}