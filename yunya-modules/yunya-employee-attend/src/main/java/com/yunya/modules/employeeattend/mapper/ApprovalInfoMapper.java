package com.yunya.modules.employeeattend.mapper;

import com.yunya.models.employee_attend.ApprovalInfo;
import com.yunya.modules.employeeattend.form.LeaveInfoForm;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ApprovalInfoMapper extends Mapper<ApprovalInfo> {
    int batchInsert(List<ApprovalInfo> list);

    /**
     * 获取当前请假最后一级别的审批人信息
     * @return
     */
    ApprovalInfo findMin(LeaveInfoForm leaveInfoForm);

    /**
     *  根据当前登录人Id和请假信息ID 获取审批流中当前登录人的审批流程
     * @param approvalInfo
     * @return
     */
    ApprovalInfo findNow(ApprovalInfo approvalInfo);



}