package com.yunya.middletable.dao.report;

import com.yunya.models.report.StatEmpPrivilege;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StatEmpPrivilegeMapper extends Mapper<StatEmpPrivilege> {
    void insertBatch(@Param("list") List<StatEmpPrivilege> datas);
}