package com.yunya.modules.employeeattend.mapper;



import com.yunya.models.employee_attend.ClinicSchedule;

import com.yunya.modules.employeeattend.vo.ClinicScheduleBaseVO;
import com.yunya.modules.employeeattend.vo.ClinicScheduleVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicScheduleMapper extends Mapper<ClinicSchedule> {

    /**
     * 获取VO对象
     *
     * @param clinicId
     * @return
     */
    List<ClinicScheduleVO> selectVOsByClinicId(@Param("clinicId") Integer clinicId);

    /**
     * 获取VO对象
     *
     * @param clinicId
     * @return
     */
    List<ClinicScheduleVO> selectVOsByClinicIdAndInservice(@Param("clinicId") Integer clinicId);

    /**
     * 批量添加
     *
     * @param clinicSchedules
     */
    int batchInsert(@Param("list") List<ClinicSchedule> clinicSchedules);

    /**
     * 获取VO对象
     *
     * @param scheduleId
     * @return
     */
    List<ClinicScheduleBaseVO> findVOByScheduleIdAndInservice(@Param("scheduleId") Integer scheduleId);
}