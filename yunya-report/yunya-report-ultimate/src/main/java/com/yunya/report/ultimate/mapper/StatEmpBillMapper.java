package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
import com.yunya.models.report.StatEmpBill;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StatEmpBillMapper extends Mapper<StatEmpBill> {
    /**
     * 查询账单时项目数量
     *
     * @param query
     * @return
     */
    List<StatEmpBill> selectBillItemNum(@Param("query") MultiClinicDateRangeQueryForm query);

    List<BillExecutorItemVO> selectStatisticsEmployeeBillWorkload(
            @Param("query") MultiClinicDateRangeQueryForm query,
            @Param("employeeIds") List<Integer> employeeIds);
}