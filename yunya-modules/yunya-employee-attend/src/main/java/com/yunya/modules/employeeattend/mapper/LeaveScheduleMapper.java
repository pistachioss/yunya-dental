package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.AttendanceStatisticsQueryForm;
import com.yunya.feign.employee_attend.vo.LeaveScheduleVO;
import com.yunya.models.employee_attend.LeaveSchedule;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface LeaveScheduleMapper extends Mapper<LeaveSchedule> {
    int batchInsert(List<LeaveSchedule> list);

    int selectNum(List<LeaveSchedule>list);

    /**
     * 根据条件查询请假时间内排班关联
     * @param queryForm 条件参数
     * @return
     */
    List<LeaveScheduleVO> findLeaveScheduleByStatisticsQuery(@Param("queryForm") AttendanceStatisticsQueryForm queryForm);
}