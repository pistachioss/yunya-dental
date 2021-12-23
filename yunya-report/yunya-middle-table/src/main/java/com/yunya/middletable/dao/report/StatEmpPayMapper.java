package com.yunya.middletable.dao.report;


import com.yunya.models.report.StatEmpPay;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StatEmpPayMapper extends Mapper<StatEmpPay> {
    void insertBatch(@Param("list") List<StatEmpPay> list);
}