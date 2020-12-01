package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.AttendanceManualMakeupQueryForm;
import com.yunya.feign.employee_attend.vo.AttendanceManualMakeupVO;
import com.yunya.models.employee_attend.AttendanceManualMakeup;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AttendanceManualMakeupMapper extends Mapper<AttendanceManualMakeup> {

    @Override
    int insert(AttendanceManualMakeup attendanceManualMakeup);

    /**
     * 分页条件查询
     *
     * @param queryForm 查询参数
     * @return
     */
    List<AttendanceManualMakeupVO> findAttendanceManualMakeupList(@Param("queryForm") AttendanceManualMakeupQueryForm queryForm);
}