package com.yunya.middletable.service.treatment_other;

import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.middletable.dao.employee_attend.BaseScheduleMapper;
import com.yunya.middletable.dao.employee_attend.EmployeeScheduleMapper;
import com.yunya.middletable.dao.report.BaseEmployeeScheduleMapper;
import com.yunya.models.employee_attend.BaseSchedule;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.models.report.BaseEmployeeSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 简介:排班消息业务层
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseEmployeeScheduleBiz extends BaseBiz<BaseEmployeeScheduleMapper, BaseEmployeeSchedule> {
    @Autowired
    private BaseScheduleMapper baseScheduleMapper;
    @Autowired
    private EmployeeScheduleMapper employeeScheduleMapper;
    /** 班次类型：上班*/
    private static final String SHIFT_WORK = "上班";
    /**
     * 排班消息
     *
     * @param msg 消息参数
     */
    public void operateEmployeeSchedule(MessageModel msg) {
        System.out.println("排班消息开始消费");
        Integer operateType = msg.getOperateType();
        Map<String, Object> paramMap = msg.getParamMap();
        Integer employeeScheduleId = (Integer) paramMap.get("id");
        switch (operateType) {
            case 0:
            case 1:
            case 2:
                // 移除旧的排班信息
                BaseEmployeeSchedule baseEmployeeSchedule = new BaseEmployeeSchedule();
                baseEmployeeSchedule.setEmployeeScheduleId(employeeScheduleId);
                mapper.delete(baseEmployeeSchedule);

                // 同步新的排班信息
                EmployeeSchedule employeeScheduleQueryForm = new EmployeeSchedule();
                employeeScheduleQueryForm.setId(employeeScheduleId);
                employeeScheduleQueryForm = employeeScheduleMapper.selectOne(employeeScheduleQueryForm);
                if(null != employeeScheduleQueryForm) {
                    BaseSchedule baseSchedule = new BaseSchedule();
                    baseSchedule.setId(employeeScheduleQueryForm.getScheduleId());
                    //班次
                    baseSchedule = baseScheduleMapper.selectOne(baseSchedule);
                    int scheduleDuration = 0;
                    if (baseSchedule.getSecondEndTime() == null) {
                        baseEmployeeSchedule.setOffWorkTime(baseSchedule.getFirstEndTime());
                        scheduleDuration =
                                (int) ((baseSchedule.getFirstEndTime().getTime() - baseSchedule.getFirstStartTime().getTime())
                                        / (1000 * 60));
                    } else {
                        baseEmployeeSchedule.setOffWorkTime(baseSchedule.getSecondEndTime());
                        scheduleDuration =
                                (int) ((baseSchedule.getSecondEndTime().getTime() - baseSchedule.getFirstStartTime().getTime())
                                        / (1000 * 60));
                    }
                    baseEmployeeSchedule.setScheduleDate(employeeScheduleQueryForm.getWorkDate());
                    baseEmployeeSchedule.setScheduleDuration(scheduleDuration);
                    baseEmployeeSchedule.setScheduleId(baseSchedule.getId());
                    baseEmployeeSchedule.setStartWorkTime(baseSchedule.getFirstStartTime());
                    baseEmployeeSchedule.setUserId(employeeScheduleQueryForm.getEmployeeId());
                    baseEmployeeSchedule.setOrgId(employeeScheduleQueryForm.getClinicId());
                    baseEmployeeSchedule.setType(baseSchedule.getType().trim().equals(SHIFT_WORK));
                    mapper.insertSelective(baseEmployeeSchedule);
                }
                break;
            default:
                break;
        }
    }
}
