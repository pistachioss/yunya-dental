package com.yunya.modules.employeeattend.biz;

import cn.hutool.core.bean.BeanUtil;
import com.yunya.feign.employee_attend.form.AttendanceManualMakeupForm;
import com.yunya.feign.employee_attend.model.AttendanceManualMakeupModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.AttendanceManualMakeup;
import com.yunya.modules.employeeattend.mapper.AttendanceManualMakeupMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 简介：考勤手动补入时长业务层
 *
 * @author: chenlin
 * @Description: 考勤手动补入时长业务层
 * @Date: 2020/11/28 12:55
 * @since: 1.0.0
 */
@Service
@Transactional
public class AttendanceManualMakeupBiz extends BaseBiz<AttendanceManualMakeupMapper, AttendanceManualMakeup> {

    /**
     * 添加手动补入时长信息
     *
     * @param attendanceManualMakeupModel 手动补入时长模型
     * @return
     */
    public Integer add(AttendanceManualMakeupModel attendanceManualMakeupModel) {
        Date now = new Date(System.currentTimeMillis());
        AttendanceManualMakeup attendanceManualMakeup = new AttendanceManualMakeup();
        BeanUtils.copyProperties(attendanceManualMakeupModel, attendanceManualMakeup);
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        attendanceManualMakeup.setCrtId(userId);
        attendanceManualMakeup.setCrtTime(now);
        attendanceManualMakeup.setUptId(userId);
        attendanceManualMakeup.setUptTime(now);
        int count = mapper.insert(attendanceManualMakeup);
        if (count != 1) {
            throw new ClientServiceException("插入数据失败", OperationCodeConstants.INSERT_MODEL);
        }
        return attendanceManualMakeup.getId();
    }

    public void update(AttendanceManualMakeupForm attendanceManualMakeupForm) {
        AttendanceManualMakeup attendanceManualMakeup = new AttendanceManualMakeup();
        BeanUtil.copyProperties(attendanceManualMakeupForm, attendanceManualMakeup);
        updateSelectiveById(attendanceManualMakeup);
    }
}
