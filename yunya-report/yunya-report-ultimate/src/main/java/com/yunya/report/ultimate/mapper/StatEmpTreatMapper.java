package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.models.report.StatEmpTreat;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StatEmpTreatMapper extends Mapper<StatEmpTreat> {
    /**
     * 统计就诊次数
     *
     * @param query
     * @param orgIds 非空时，根据门诊分组统计
     * @param employeeIds 非空时，根据员工分组统计
     * @return
     */
    List<StatEmpTreat> selectClinicTreatVisitNum(
            @Param("query") DateRangeQueryForm query,
            @Param("orgIds") List<Integer> orgIds,
            @Param("employeeIds") List<Integer> employeeIds);
}