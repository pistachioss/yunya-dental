package com.yunya.modules.employeeattend.controller;


import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.modules.employeeattend.form.EmployeeScheduleCopyForm;
import com.yunya.modules.employeeattend.form.EmployeeScheduleDeleteForm;
import com.yunya.modules.employeeattend.form.EmployeeScheduleForm;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import com.yunya.modules.employeeattend.biz.EmployeeScheduleBiz;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleExportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleController
 * @description
 * @date 2020/7/9 10:27
 */
@Api(tags = "排班接口")
@RestController
@RequestMapping("/employee_schedule")
@CrossOrigin
public class EmployeeScheduleController {
  @Autowired
  private EmployeeScheduleBiz employeeScheduleBiz;

  /**
   * 删除排班表
   *
   * @param employeeScheduleDeleteForm
   */
  @DeleteMapping
  @ApiOperation("删除排班表")
  public ResponseResult delete(@RequestBody @Validated EmployeeScheduleDeleteForm employeeScheduleDeleteForm) {

    EmployeeSchedule employeeSchedule = EntityUtils.build(employeeScheduleDeleteForm, EmployeeSchedule.class);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
    try {
      employeeSchedule.setWorkDate(simpleDateFormat.parse(employeeScheduleDeleteForm.getWorkDateString()));
    } catch (ParseException e) {
      throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
    }
    employeeScheduleBiz.delete(employeeSchedule);
    return ResponseUtil.success();
  }

  /**
   * 查看员工排班列表
   * @param employeeScheduleQueryForm
   * @return
   */
  @PostMapping("/findList")
  @ApiOperation("查看员工排班列表")
  public ResponseResult findList(@RequestBody @Validated EmployeeScheduleQueryForm employeeScheduleQueryForm) {
    return ResponseUtil.success(employeeScheduleBiz.findList(employeeScheduleQueryForm));
  }

  /**
   * 复制排班表
   *
   * @param employeeScheduleCopyForm
   * @return
   */
  @PostMapping("/copy")
  @ApiOperation("复制排班表")
  public ResponseResult copy(@RequestBody @Validated EmployeeScheduleCopyForm employeeScheduleCopyForm) {
    return ResponseUtil.success(employeeScheduleBiz.copy(employeeScheduleCopyForm));
  }

  /**
   * 添加
   *
   * @param employeeScheduleForm
   */
  @PostMapping("/create")
  @ApiOperation("添加")
  public ResponseResult create(@RequestBody EmployeeScheduleForm employeeScheduleForm) throws ParseException {
    employeeScheduleBiz.create(employeeScheduleForm);
    return ResponseUtil.success();
  }


  /**
   * 导出员工排班
   *
   * @param
   * @throws Exception
   */
  @PostMapping("/export")
  @ApiOperation("导出员工排班")
  public void export(
          HttpServletResponse response, @RequestBody EmployeeScheduleQueryForm employeeScheduleQueryForm
  ) throws Exception {
    employeeScheduleBiz.export(response, employeeScheduleQueryForm);
  }

  /**
   * 导出复制排班冲突
   *
   * @param
   * @throws Exception
   */
  @PostMapping("/exportconflict")
  @ApiOperation("导出复制排班冲突")
  public void exportConflict(
          HttpServletResponse response,
          List<EmployeeScheduleExportVO>employeeConflict
  ) throws Exception {
    employeeScheduleBiz.exportConflict(response, employeeConflict);
  }

}
