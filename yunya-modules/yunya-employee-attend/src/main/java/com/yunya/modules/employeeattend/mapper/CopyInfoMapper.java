package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.AttendanceItemCountQuery;
import com.yunya.feign.employee_attend.vo.AttendanceItemCountVO;
import com.yunya.models.employee_attend.CopyInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CopyInfoMapper extends Mapper<CopyInfo> {

    int batchInsert(@Param("list")List<CopyInfo> list);

    AttendanceItemCountVO selectAssociatedItemCount(@Param("query") AttendanceItemCountQuery query);

    AttendanceItemCountVO selectAttendanceItemCount(@Param("query") AttendanceItemCountQuery query);
}