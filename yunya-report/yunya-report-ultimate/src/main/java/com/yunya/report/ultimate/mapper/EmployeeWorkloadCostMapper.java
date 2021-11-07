package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.ClinicEmployeeWorkloadQuery;
import com.yunya.models.report.EmployeeWorkloadCost;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface EmployeeWorkloadCostMapper extends Mapper<EmployeeWorkloadCost> {

    /**
     * 查询员工的加工费、正畸加工费、大额材料费
     *
     * @param query
     * @return
     */
    List<EmployeeWorkloadCost> selectClinicEmployeeWorkCost(@Param("query") ClinicEmployeeWorkloadQuery query);
}