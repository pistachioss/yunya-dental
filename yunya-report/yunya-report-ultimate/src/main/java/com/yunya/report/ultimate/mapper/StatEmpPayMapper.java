package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.models.report.StatEmpPay;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StatEmpPayMapper extends Mapper<StatEmpPay> {

    List<StatEmpPay> selectClinicReceivedWorkload(
            @Param("query") DateRangeQueryForm query,
            @Param("orgIds") List<Integer> orgIds,
            @Param("employeeIds") List<Integer> employeeIds);
}