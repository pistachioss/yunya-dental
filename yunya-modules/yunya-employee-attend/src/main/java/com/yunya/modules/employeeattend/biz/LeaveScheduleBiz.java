package com.yunya.modules.employeeattend.biz;

import com.yunya.feign.employee_attend.form.AttendanceStatisticsQueryForm;
import com.yunya.feign.employee_attend.vo.LeaveScheduleVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.employee_attend.LeaveSchedule;
import com.yunya.modules.employeeattend.mapper.LeaveScheduleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简介：请假时间内排班关联业务层
 *
 * @author: chenlin
 * @Description: 请假时间内排班关联业务层
 * @Date: 2020/11/30 14:46
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LeaveScheduleBiz extends BaseBiz<LeaveScheduleMapper, LeaveSchedule> {

    /**
     * 根据条件查询请假时间内排班关联
     * @param queryForm 条件参数
     * @return
     */
    public List<LeaveScheduleVO> findLeaveScheduleByStatisticsQuery(AttendanceStatisticsQueryForm queryForm) {
        return mapper.findLeaveScheduleByStatisticsQuery(queryForm);
    }
}
