package com.yunya.middletable.service.treatment_other;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.vo.BaseEmployeeScheduleVO;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.middletable.dao.report.BaseEmployeeScheduleMapper;
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
    private EmployeeAttendServiceFeign employeeAttendServiceFeign;
    /**
     * 排班消息
     *
     * @param msg 消息参数
     */
    public void operateEmployeeSchedule(MessageModel msg) {
        Map<String, Object> paramMap = msg.getParamMap();
        Integer id = (Integer) paramMap.get("id");
        EmployeeSchedule employeeScheduleQueryForm = new EmployeeSchedule();
        employeeScheduleQueryForm.setId(id);
        BaseEmployeeScheduleVO employeeScheduleVO = employeeAttendServiceFeign.findEmInfoById(employeeScheduleQueryForm);
        Integer operateType = msg.getOperateType();
        BaseEmployeeSchedule baseEmployeeSchedule = new BaseEmployeeSchedule();
        BeanUtils.copyProperties(employeeScheduleVO,baseEmployeeSchedule);
        switch (operateType) {
            case 0:
                if (null != employeeScheduleVO.getEmployeeScheduleId()) {
                    mapper.insertSelective(baseEmployeeSchedule);
                }
                break;
            case 2:
                if (null != employeeScheduleVO.getEmployeeScheduleId()) {
                    mapper.delete(baseEmployeeSchedule);
                }
                break;
            default:
                break;
        }
    }

}
