package com.yunya.middletable.service.treatment_other;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.vo.BaseEmployeeScheduleVO;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.middletable.dao.employee_attend.BaseScheduleMapper;
import com.yunya.middletable.dao.employee_attend.EmployeeScheduleMapper;
import com.yunya.middletable.dao.report.BaseEmployeeScheduleMapper;
import com.yunya.models.employee_attend.BaseSchedule;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.models.report.BaseEmployeeSchedule;
import org.springframework.beans.BeanUtils;
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
    /**
     * 排班消息
     *
     * @param msg 消息参数
     */
    public void operateEmployeeSchedule(MessageModel msg) {
        BaseEmployeeScheduleVO employeeScheduleVO = new BaseEmployeeScheduleVO();

        Map<String, Object> paramMap = msg.getParamMap();
        Integer id = (Integer) paramMap.get("id");
        EmployeeSchedule employeeScheduleQueryForm = new EmployeeSchedule();
        employeeScheduleQueryForm.setId(id);
        //排班信息
        employeeScheduleQueryForm = employeeScheduleMapper.selectOne(employeeScheduleQueryForm);
        BaseSchedule baseSchedule = new BaseSchedule();
        baseSchedule.setId(employeeScheduleQueryForm.getScheduleId());
        //班次
        baseSchedule = baseScheduleMapper.selectOne(baseSchedule);

        Integer operateType = msg.getOperateType();
        BaseEmployeeSchedule baseEmployeeSchedule = new BaseEmployeeSchedule();
        baseEmployeeSchedule.setEmployeeScheduleId(id);
        baseEmployeeSchedule.setOrgId(employeeScheduleQueryForm.getClinicId());
        int scheduleDuration = 0;
        if(baseSchedule.getSecondEndTime()==null){
            baseEmployeeSchedule.setOffWorkTime(baseSchedule.getFirstEndTime());
            scheduleDuration =
               (int)((baseSchedule.getFirstEndTime().getTime()-baseSchedule.getFirstStartTime().getTime())
                       /(1000 * 60));
        }else{
            baseEmployeeSchedule.setOffWorkTime(baseSchedule.getSecondEndTime());
            scheduleDuration =
                    (int)((baseSchedule.getSecondEndTime().getTime()-baseSchedule.getFirstStartTime().getTime())
                            /(1000 * 60));
        }
        baseEmployeeSchedule.setScheduleDate(employeeScheduleQueryForm.getWorkDate());
        baseEmployeeSchedule.setScheduleDuration(scheduleDuration);
        baseEmployeeSchedule.setScheduleId(baseSchedule.getId());
        baseEmployeeSchedule.setStartWorkTime(baseSchedule.getFirstStartTime());
        baseEmployeeSchedule.setUserId(employeeScheduleQueryForm.getEmployeeId());

        switch (operateType) {
            case 0:
                if (null != employeeScheduleVO.getEmployeeScheduleId()) {
                    mapper.insertSelective(baseEmployeeSchedule);
                }
                break;
            case 2:
                if (null != employeeScheduleVO.getEmployeeScheduleId()) {
                    baseEmployeeSchedule = new BaseEmployeeSchedule();
                    baseEmployeeSchedule.setEmployeeScheduleId(id);
                    mapper.delete(baseEmployeeSchedule);
                }
                break;
            default:
                break;
        }
    }

}
