package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.AttendancePunchRecordQueryForm;
import com.yunya.feign.employee_attend.vo.AttendancePunchRecordVO;
import com.yunya.feign.employee_attend.vo.AttendanceWorkDateOvertimeMinuteVO;
import com.yunya.models.employee_attend.AttendancePunchRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AttendancePunchRecordMapper extends Mapper<AttendancePunchRecord> {
    /**
     * 分页查询员工的考勤打卡记录列表
     *
     * @param queryForm 查询参数
     * @return
     */
    List<AttendancePunchRecordVO> findAttendancePunchRecordList(@Param("queryForm") AttendancePunchRecordQueryForm queryForm);

    /**
     * 分页查询员工的考勤打卡记录列表（带排班id）
     *
     * @param queryForm 查询参数
     * @return
     */
    List<AttendancePunchRecordVO> findAttendancePunchRecordWithScheduleIdList(@Param("queryForm") AttendancePunchRecordQueryForm queryForm);

    /**
     * 分页查询工作日加班时长的考勤汇总明细
     *
     * @param minute 工作日加班时长
     * @param queryForm 查询参数
     * @return
     */
    List<AttendanceWorkDateOvertimeMinuteVO> statisticsWorkDateOvertimeByMinute(@Param("minute") int minute, @Param("queryForm") AttendancePunchRecordQueryForm queryForm);

    /**
     * 根据打卡日期分组，分页查询打卡记录列表
     * @param queryForm 查询参数
     * @return
     */
    List<AttendancePunchRecordVO> attendancePunchRecordListGroupByDate(@Param("queryForm") AttendancePunchRecordQueryForm queryForm);
}