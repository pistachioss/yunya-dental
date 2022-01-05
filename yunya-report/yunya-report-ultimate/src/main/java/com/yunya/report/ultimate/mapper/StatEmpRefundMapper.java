package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.models.report.StatEmpRefund;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StatEmpRefundMapper extends Mapper<StatEmpRefund> {
    List<BillExecutorItemVO> selectStatisticsEmployeeRefundWorkload(
            @Param("query") MultiClinicDateRangeQueryForm query,
            @Param("employeeIds") List<Integer> employeeIds);
}