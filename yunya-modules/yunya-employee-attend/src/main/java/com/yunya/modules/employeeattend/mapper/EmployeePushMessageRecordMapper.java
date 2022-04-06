package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.EmployeePushMessageRecordQueryForm;
import com.yunya.feign.employee_attend.vo.EmployeePushMessageRecordVO;
import com.yunya.models.employee_attend.EmployeePushMessageRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface EmployeePushMessageRecordMapper extends Mapper<EmployeePushMessageRecord> {

    List<EmployeePushMessageRecordVO> selectPushMessageRecordList(@Param("query") EmployeePushMessageRecordQueryForm query);
}