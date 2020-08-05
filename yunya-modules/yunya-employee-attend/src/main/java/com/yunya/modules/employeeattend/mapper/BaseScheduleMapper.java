package com.yunya.modules.employeeattend.mapper;




import com.yunya.models.employee_attend.BaseSchedule;
import com.yunya.modules.employeeattend.form.ScheduleForm;
import com.yunya.modules.employeeattend.vo.BaseInserviceVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseScheduleMapper extends Mapper<BaseSchedule> {
    /**
     * 排班门诊列表
     *
     * @return
     */
    List<BaseInserviceVO> selectInserviceVOsByScheduleId(@Param("id") Integer scheduleId);

    /**
     * 查询列表
     * @param
     * @param
     * @return
     */
    List<BaseSchedule> selectByTypeAndName(ScheduleForm scheduleForm);

    /**
     * 修改
     *
     * @param baseSchedule
     */
    void updateById(BaseSchedule baseSchedule);
}