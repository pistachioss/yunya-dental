package com.yunya.modules.employeeattend.rpc;

import com.yunya.feign.employee_attend.vo.BaseEmployeeScheduleVO;
import com.yunya.feign.employee_attend.vo.LeaveInfoListVO;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.modules.employeeattend.biz.LeaveInfoBiz;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import com.yunya.modules.employeeattend.form.LeaveInfoForm;
import com.yunya.modules.employeeattend.rpc.service.EmployeeScheduleSerivce;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 杨柳絮
 * @className EmployeeAttendServiceRest
 * @description
 * @date 2020/7/23 16:00
 */
@Api("排班服务接口暴露")
@RestController
@RequestMapping("api")
public class EmployeeAttendServiceRest {

  @Autowired private EmployeeScheduleSerivce employeeScheduleSerivce;
  @Autowired private LeaveInfoBiz leaveInfoBiz;
  /**
   * 查看员工排班列表
   * @param employeeScheduleQueryForm
   * @return
   */
  @RequestMapping(value = "/employee/attend/list", method = RequestMethod.POST)
  public EmployeeScheduleResultVO findList(@RequestBody @Validated EmployeeScheduleQueryForm employeeScheduleQueryForm) {
    EmployeeScheduleResultVO employeeScheduleResultVO = employeeScheduleSerivce.findList(employeeScheduleQueryForm);
    return employeeScheduleResultVO;
  }
  /**
   * 根据排班Id查看排班信息
   * @param employeeSchedule
   * @return
   */
  @RequestMapping(value = "/employee/attend/findEmInfoById", method = RequestMethod.POST)
  public BaseEmployeeScheduleVO findEmInfoById(@RequestBody @Validated EmployeeSchedule employeeSchedule) {
    BaseEmployeeScheduleVO baseEmployeeScheduleVO = employeeScheduleSerivce.findEmInfoById(employeeSchedule.getId());
    return baseEmployeeScheduleVO;
  }

  /**
   * 获取请假申请列表
   *
   * @param
   * @return
   */
  @RequestMapping(value = "/leave/info/findList",method=RequestMethod.POST)
  public List<LeaveInfoListVO> findEmployeeLeaveInfoList(@RequestBody @Validated LeaveInfoForm leaveInfoForm) {
    return this.leaveInfoBiz.findList(leaveInfoForm);
  }

  /**
   * 根据申请人ID集合获取请假申请列表
   *
   * @param
   * @return
   */
  @ApiOperation("获取请假申请列表")
  @RequestMapping(value = "/leave_info/findListByIds",method = RequestMethod.POST)
  public List<LeaveInfoListVO> findListByIds(@RequestBody @Validated LeaveInfoForm leaveInfoForm) {
    return leaveInfoBiz.findListByDateAndIds(leaveInfoForm);
  }

}
