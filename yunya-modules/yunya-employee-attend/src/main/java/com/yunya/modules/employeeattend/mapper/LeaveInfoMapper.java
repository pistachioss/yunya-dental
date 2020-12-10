package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.LeaveInfoQueryForm;
import com.yunya.feign.employee_attend.vo.*;
import com.yunya.models.employee_attend.ApprovalLevelSet;
import com.yunya.models.employee_attend.LeaveInfo;
import com.yunya.modules.employeeattend.form.FindApprovalByMeForm;
import com.yunya.modules.employeeattend.form.LeaveInfoFindForm;
import com.yunya.modules.employeeattend.form.LeaveInfoForm;
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
    List<LeaveInfo>findLeaveListByDay(LeaveInfo leaveInfo);

    /**
     * 查询按天请假时包含的班次
     * @param leaveInfoForm
     * @return
     */
    List<EmLeaveVO>selectBaseByDay(LeaveInfoForm leaveInfoForm);

    /**
     * 根据天数获得审批信息
     * @param leaveInfoForm
     * @return
     */
    List<ApprovalLevelSet>selectApprovalByDay(LeaveInfoForm leaveInfoForm);

    /**
     * 判断员工某天是否有请假申请
     * @return
     */
    int countByDay(LeaveInfo leaveInfo);

    /**
     * 判断时间段内是否有除了请假外其他类型的申请
     * @param leaveInfo
     * @return
     */
    int countFiWi(LeaveInfo leaveInfo);

    List<LeaveInfoListVO>selectLeave(LeaveInfoForm leaveInfoForm);

    /**
     * 根据请假ID获取审批信息
     * @param leaveInfoForm
     * @return
     */
    List<ApprovalInfoVO>findApproval(LeaveInfoForm leaveInfoForm);

    /**
     * 待我审批或已经审批列表
     * @param findApprovalByMeForm
     * @return
     */
    List<LeaveAppVO> findApprovalByMe(FindApprovalByMeForm findApprovalByMeForm);

    /**
     * 待我审批或已经审批列表
     * @param findApprovalByMeForm
     * @return
     */
    List<LeaveAppVO> findOverApprovalByMe(FindApprovalByMeForm findApprovalByMeForm);

    /**
     * 分页查询请假时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return
     */
    List<LeaveInfoVO> statisticsLeavesByMinute(@Param("queryForm") LeaveInfoQueryForm queryForm);

    /**
     * 条件查询按班次请假的申请
     *
     * @param queryForm 查询参数
     * @return
     */
    List<LeaveInfoVO> findLeaveInfosBySchedule(@Param("queryForm") LeaveInfoQueryForm queryForm);


}