package com.yunya.feign.employee_attend;

import com.yunya.feign.employee_attend.factory.EmployeeAttendServiceFallBackFactory;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.form.FieldInfoForm;
import com.yunya.feign.employee_attend.form.LeaveInfoForm;
import com.yunya.feign.employee_attend.form.WorkOvertimeInfoForm;
import com.yunya.feign.employee_attend.vo.*;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.models.employee_attend.LeaveInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_EMPLOYEE_ATTEND,
        fallbackFactory = EmployeeAttendServiceFallBackFactory.class)
public interface EmployeeAttendServiceFeign {
  /**
   * 排班列表暴露接口
   *
   * @param employeeScheduleQueryForm 参数封装
   * @return
   */
  @RequestMapping(value = "/api/employee/attend/list", method = RequestMethod.POST)
  EmployeeScheduleResultVO findList(@RequestBody EmployeeScheduleQueryForm employeeScheduleQueryForm);
  /**
   * 中间表排班信息暴露接口
   *
   * @param employeeSchedule 参数封装
   * @return
   */
  @RequestMapping(value = "/api/employee/attend/findEmInfoById", method = RequestMethod.POST)
  BaseEmployeeScheduleVO findEmInfoById(@RequestBody EmployeeSchedule employeeSchedule);

  /**
   * 获取请假申请列表
   *
   * @param
   * @return
   */
  @RequestMapping(value = "/api/leave/info/findList",method=RequestMethod.POST)
  public List<LeaveInfoListVO> findEmployeeLeaveInfoList(@RequestBody @Validated LeaveInfoForm leaveInfoForm);

  /**
   * 根据申请人ID集合获取请假申请列表
   *
   * @param
   * @return
   */
  @ApiOperation("获取请假申请列表")
  @RequestMapping(value = "api/leave_info/findListByIds",method = RequestMethod.POST)
  public List<LeaveInfoListVO> findListByIds(@RequestBody @Validated LeaveInfoForm leaveInfoForm);

  /**
   * 根据申请人ID集合获取请假申请列表(后端使用)
   *
   * @param
   * @return
   */
  @ApiOperation("获取请假申请列表")
  @RequestMapping(value = "api/leave_info/back/findListByIds",method = RequestMethod.POST)
  public List<LeaveInfoListVO> backFindListByIds(@RequestBody @Validated LeaveInfoForm leaveInfoForm);

  /**
   * 获取加班列表
   *
   * @param
   * @return
   */
  @ApiOperation("获取请假申请列表")
  @RequestMapping(value = "api/work_overtime_info/findList",method = RequestMethod.POST)
  public List<WorkOvertimeInfoListVO> workFindList(@RequestBody @Validated WorkOvertimeInfoForm workOvertimeInfoForm);

  /**
   * 获取外勤列表
   *
   * @param
   * @return
   */
  @ApiOperation("获取外勤申请列表")
  @RequestMapping(value = "api/field_info/findList",method = RequestMethod.POST)
  public List<FieldInfoListVO> fieldFindList(@RequestBody @Validated FieldInfoForm fieldInfoForm);
}
