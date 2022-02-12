package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.models.report.StatEmpPrivilege;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StatEmpPrivilegeMapper extends Mapper<StatEmpPrivilege> {
    List<BillExecutorItemVO> selectStatisticsEmployeeCouponWorkload(
            @Param("query") MultiClinicDateRangeQueryForm query,
            @Param("employeeIds") List<Integer> employeeIds);
}