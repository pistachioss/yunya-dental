package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.AttendancePunchRecordQueryForm;
import com.yunya.feign.employee_attend.form.AttendanceStatisticsQueryForm;
import com.yunya.feign.employee_attend.vo.AttendancePunchRecordVO;
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
     * 根据条件分页查询考勤汇总
     *
     * @param queryForm
     * @return
     */
    List<AttendancePunchRecordVO> selectAttendanceStatisticsPunchRecord(@Param("queryForm") AttendanceStatisticsQueryForm queryForm);
}