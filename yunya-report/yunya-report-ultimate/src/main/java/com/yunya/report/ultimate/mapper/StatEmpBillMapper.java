package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.models.report.StatEmpBill;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StatEmpBillMapper extends Mapper<StatEmpBill> {
    List<StatEmpBill> selectBillItemList(@Param("query") MultiClinicDateRangeQueryForm query);
}