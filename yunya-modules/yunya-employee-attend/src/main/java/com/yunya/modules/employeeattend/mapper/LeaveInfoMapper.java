package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.LeaveInfoQueryForm;
import com.yunya.feign.employee_attend.vo.LeaveInfoVO;
import com.yunya.models.employee_attend.LeaveInfo;
import com.yunya.modules.employeeattend.form.LeaveInfoFindForm;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface LeaveInfoMapper extends Mapper<LeaveInfo> {

    /**
     * 根据日期和用户id列表查询请假列表
     * @param userIds 用户id列表
     * @param date 日期
     * @return
     */
    List<LeaveInfoVO> findLeaveInfosByUserIdsAndDate(@Param("userIds") List<Integer> userIds, @Param("date") Date date);

    /**
     * 根据条件分页查询请假列表
     * @param queryForm
     * @return
     */
    List<LeaveInfoVO> findLeaveInfoList(@Param("queryForm") LeaveInfoQueryForm queryForm);

    /**
     * 根据用户Id查询处于审批中或已通过的按天请假的申请
     * @return
     */
    List<LeaveInfoVO>findLeaveListByDay(LeaveInfoFindForm leaveInfoFindForm);
}