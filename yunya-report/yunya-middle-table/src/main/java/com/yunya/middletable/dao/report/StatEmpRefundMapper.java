package com.yunya.middletable.dao.report;

import com.yunya.models.report.StatEmpRefund;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StatEmpRefundMapper extends Mapper<StatEmpRefund> {
    void insertBatch(@Param("list") List<StatEmpRefund> datas);
}