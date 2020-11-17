package com.yunya.modules.employeeattend.mapper;

import com.yunya.models.employee_attend.CopyInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CopyInfoMapper extends Mapper<CopyInfo> {

    int batchInsert(@Param("list")List<CopyInfo> list);
}