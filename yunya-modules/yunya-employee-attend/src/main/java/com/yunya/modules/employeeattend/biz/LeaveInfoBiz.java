package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.employee_attend.form.LeaveInfoQueryForm;
import com.yunya.feign.employee_attend.vo.LeaveInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.employee_attend.LeaveInfo;
import com.yunya.modules.employeeattend.mapper.LeaveInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 简介：请假信息业务层
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/10 20:50
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LeaveInfoBiz extends BaseBiz<LeaveInfoMapper, LeaveInfo> {

    /**
     * 根据日期和用户id列表查询请假列表
     * @param userIds 用户id列表
     * @param date 日期
     * @return
     */
    public List<LeaveInfoVO> findLeaveInfosByUserIdsAndDate(List<Integer> userIds, Date date) {
        return mapper.findLeaveInfosByUserIdsAndDate(userIds, date);
    }

    /**
     * 根据条件分页查询请假列表
     * @param queryForm
     * @return
     */
    public List<LeaveInfoVO> findLeaveInfoList(LeaveInfoQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        return mapper.findLeaveInfoList(queryForm);
    }
}
