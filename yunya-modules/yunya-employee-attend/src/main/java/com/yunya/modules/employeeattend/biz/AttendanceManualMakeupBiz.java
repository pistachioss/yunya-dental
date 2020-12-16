package com.yunya.modules.employeeattend.biz;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.yunya.feign.employee_attend.form.AttendanceManualMakeupForm;
import com.yunya.feign.employee_attend.form.AttendanceManualMakeupQueryForm;
import com.yunya.feign.employee_attend.model.AttendanceManualMakeupModel;
import com.yunya.feign.employee_attend.vo.AttendanceManualMakeupVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.employee_attend.AttendanceManualMakeup;
import com.yunya.modules.employeeattend.mapper.AttendanceManualMakeupMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

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
     * 手动补入时长信息
     *
     * @param attendanceManualMakeupModel 手动补入时长模型
     * @return
     */
    public Integer update(AttendanceManualMakeupModel attendanceManualMakeupModel) {
        Integer minute = attendanceManualMakeupModel.getMinute();
        if (minute != null) {
            Pattern p = Pattern.compile("^\\d{1,11}$");
            Matcher mathcer = p.matcher(minute+"");
            if(!mathcer.matches() || minute<=0) {
                throw new ClientServiceException("补入工作时长只能输入10位以内的正整数", PARAMETERS_IS_ILLEGAL);
            }
        }
        String desc = attendanceManualMakeupModel.getMakeupDesc();
        if (StringHelper.isNotEmpty(desc)) {
            if (desc.length() > 50) {
                throw new ClientServiceException("补入时长说明不能超过50个字符", PARAMETERS_IS_ILLEGAL);
            }
        }
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = new Date(System.currentTimeMillis());
        AttendanceManualMakeup attendanceManualMakeup = new AttendanceManualMakeup();
        BeanUtils.copyProperties(attendanceManualMakeupModel, attendanceManualMakeup);
        attendanceManualMakeup.setUptId(userId);
        attendanceManualMakeup.setUptTime(now);
        Integer id = attendanceManualMakeupModel.getId();
        if (id == null) {
            attendanceManualMakeup.setCrtId(userId);
            attendanceManualMakeup.setCrtTime(now);
            int count = mapper.insert(attendanceManualMakeup);
            if (count != 1) {
                throw new ClientServiceException("插入数据失败", OperationCodeConstants.INSERT_MODEL);
            }
            id = attendanceManualMakeup.getId();
        } else {
            updateSelectiveById(attendanceManualMakeup);
        }
        return id;
    }

    public void update(AttendanceManualMakeupForm attendanceManualMakeupForm) {
        AttendanceManualMakeup attendanceManualMakeup = new AttendanceManualMakeup();
        BeanUtil.copyProperties(attendanceManualMakeupForm, attendanceManualMakeup);
        updateSelectiveById(attendanceManualMakeup);
    }

    /**
     * 分页条件查询
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceManualMakeupVO> findAttendanceManualMakeupList(AttendanceManualMakeupQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        return mapper.findAttendanceManualMakeupList(queryForm);
    }
}
