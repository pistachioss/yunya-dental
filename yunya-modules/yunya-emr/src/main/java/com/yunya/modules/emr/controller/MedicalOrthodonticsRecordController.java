package com.yunya.modules.emr.controller;

import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.emr.domain.query.MedicalOrthodonticsRecordQueryForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.emr.MedicalOrthodonticsRecord;
import com.yunya.modules.emr.biz.MedicalOrthodonticsRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author 杨柳絮
 * @className medicalOrthodonticsRecordController
 * @description
 * @date 2020/7/28 13:21
 */
@Api(tags = "正畸电子病历接口")
@RestController
@RequestMapping("/medical_Orthodontics")
@CrossOrigin
public class MedicalOrthodonticsRecordController {
  @Autowired private EmployeeAttendServiceFeign employeeAttendServiceFeign;
  @Autowired private MedicalOrthodonticsRecordBiz medicalOrthodonticsRecordBiz;
  /**
   * 查询正畸电子病历
   *
   * @param query
   * @return
   */
  @PostMapping("/findList")
  @ApiOperation("查询列表")
  public ResponseResult findList(@RequestBody @Valid MedicalOrthodonticsRecordQueryForm query) {
    MedicalOrthodonticsRecord medicalOrthodonticsRecord = new MedicalOrthodonticsRecord();
    BeanUtils.copyProperties(query,medicalOrthodonticsRecord);
    return ResponseUtil.success(medicalOrthodonticsRecordBiz.selectByEntity(medicalOrthodonticsRecord));
  }

  /**
   * 新增正畸电子病历
   *
   * @param model
   * @return
   */
  @PostMapping("/create")
  @ApiOperation("新增正畸电子病历")
  @CurrentUser
  public ResponseResult create(@RequestBody @Valid MedicalOrthodonticsRecord model) {

    MedicalOrthodonticsRecordQueryForm queryForm = new MedicalOrthodonticsRecordQueryForm();
    queryForm.setPatientId(model.getPatientId());
    MedicalOrthodonticsRecord medicalOrthodonticsRecord = new MedicalOrthodonticsRecord();
    BeanUtils.copyProperties(queryForm,medicalOrthodonticsRecord);
    List<MedicalOrthodonticsRecord>list = medicalOrthodonticsRecordBiz.selectByEntity(medicalOrthodonticsRecord);
    if(list.size()>0){
      return ResponseUtil.error("该患者已经拥有正畸病历",list);
    }
    model.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    model.setCrtTime(new Date());
    return ResponseUtil.success(medicalOrthodonticsRecordBiz.create(model));
  }

//  /**
//   * 查询
//   *
//   * @param
//   * @return
//   */
//  @PostMapping("/txe")
//  @ApiOperation("查询列表")
//  public ResponseResult txe() {
//    EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
//    employeeScheduleQueryForm.setClinicId(35);
//    employeeScheduleQueryForm.setEndDate("2020-08-07");
//    employeeScheduleQueryForm.setStartDate("2020-07-30");
//    return ResponseUtil.success(employeeAttendServiceFeign.findList(employeeScheduleQueryForm));
//  }
}
