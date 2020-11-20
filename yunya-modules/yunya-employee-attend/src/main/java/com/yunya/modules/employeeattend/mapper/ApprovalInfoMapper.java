package com.yunya.modules.employeeattend.mapper;

import com.yunya.models.employee_attend.ApprovalInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ApprovalInfoMapper extends Mapper<ApprovalInfo> {
    int batchInsert(List<ApprovalInfo> list);

}