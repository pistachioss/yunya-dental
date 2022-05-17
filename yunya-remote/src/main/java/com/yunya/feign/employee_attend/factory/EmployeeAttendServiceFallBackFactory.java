package com.yunya.feign.employee_attend.factory;

import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.form.FieldInfoForm;
import com.yunya.feign.employee_attend.form.LeaveInfoForm;
import com.yunya.feign.employee_attend.form.WorkOvertimeInfoForm;
import com.yunya.feign.employee_attend.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.employee_attend.AttendanceAddressSet;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.models.employee_attend.LeaveInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * 简介: 排班服务调用降级处理
 *
 * @author: chow
 * @date: 2020/7/9 12:25
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Component
public class EmployeeAttendServiceFallBackFactory implements EmployeeAttendServiceFeign {


  @Override
  public List<AttendanceAddressSet> findAddress() {
    return null;
  }

  @Override
  public EmployeeScheduleResultVO findList(EmployeeScheduleQueryForm employeeScheduleQueryForm) {
    return null;
  }
  @Override
  public BaseEmployeeScheduleVO findEmInfoById(@RequestBody EmployeeSchedule employeeSchedule){
   return null;
  }

  @Override
  public List<LeaveInfoListVO> findEmployeeLeaveInfoList(LeaveInfoForm leaveInfoForm) {
    return null;
  }

  @Override
  public List<LeaveInfoListVO> findListByIds(LeaveInfoForm leaveInfoForm) {
    return null;
  }

  @Override
  public List<LeaveInfoListVO> backFindListByIds(LeaveInfoForm leaveInfoForm) {
    return null;
  }

  @Override
  public List<WorkOvertimeInfoListVO> workFindList(@RequestBody @Validated WorkOvertimeInfoForm workOvertimeInfoForm){
    return null;
  }

  @Override
  public List<FieldInfoListVO> fieldFindList(@RequestBody @Validated FieldInfoForm fieldInfoForm){
    return null;
  }
}
