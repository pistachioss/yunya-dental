package com.yunya.middletable.dao.report;

import com.yunya.models.report.StatEmpTreat;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StatEmpTreatMapper extends Mapper<StatEmpTreat> {
    void insertBatch(@Param("list") List<StatEmpTreat> list);
}