package com.yunya.middletable.dao.report;


import com.yunya.models.report.StatEmpBill;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StatEmpBillMapper extends Mapper<StatEmpBill> {
    void insertBatch(@Param("list") List<StatEmpBill> list);
}