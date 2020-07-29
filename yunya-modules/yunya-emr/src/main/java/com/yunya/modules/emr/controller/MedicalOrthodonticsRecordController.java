package com.yunya.modules.emr.controller;

import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.emr.domain.query.MedicalOrthodonticsRecordQueryForm;
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

  @Autowired private MedicalOrthodonticsRecordBiz medicalOrthodonticsRecordBiz;
  @Autowired private EmployeeAttendServiceFeign employeeAttendServiceFeign;
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
    return ResponseUtil.success(medicalOrthodonticsRecordBiz.selectByObj(medicalOrthodonticsRecord));
  }

  /**
   * 新增正畸电子病历
   *
   * @param model
   * @return
   */
  @PostMapping("/create")
  @ApiOperation("新增数据")
  public ResponseResult create(@RequestBody @Valid MedicalOrthodonticsRecord model) {
    model.setCrtId(12);
    model.setCrtTime(new Date());
    return ResponseUtil.success(medicalOrthodonticsRecordBiz.create(model));
  }

  @PostMapping("/texy")
  @ApiOperation("查询列表")
  public ResponseResult List() {
    EmployeeScheduleQueryForm m = new EmployeeScheduleQueryForm();
    m.setClinicId(35);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    try {
      m.setStartDate(sdf.parse("2020-07-30"));
      m.setEndDate(sdf.parse("2020-08-02"));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return ResponseUtil.success(employeeAttendServiceFeign.findList(m));
  }
}
