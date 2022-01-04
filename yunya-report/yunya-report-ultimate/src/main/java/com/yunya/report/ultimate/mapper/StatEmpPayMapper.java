package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.models.report.StatEmpPay;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StatEmpPayMapper extends Mapper<StatEmpPay> {
    /**
     * 统计实收工作量
     *
     * @param query
     * @param orgIds 非空时，根据门诊分组统计
     * @param employeeIds 非空时，根据员工分组统计
     * @return
     */
    List<StatEmpPay> selectClinicReceivedWorkload(
            @Param("query") DateRangeQueryForm query,
            @Param("orgIds") List<Integer> orgIds,
            @Param("employeeIds") List<Integer> employeeIds);

    List<StatEmpPay> selectStatisticsEmployeeWorkload(@Param("query") MultiClinicDateRangeQueryForm query);
}