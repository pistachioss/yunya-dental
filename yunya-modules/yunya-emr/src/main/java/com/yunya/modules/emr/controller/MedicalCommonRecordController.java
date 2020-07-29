package com.yunya.modules.emr.controller;

import com.yunya.feign.emr.domain.model.MedicalCommonRecordModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.emr.biz.MedicalCommonRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 杨柳絮
 * @className medicalCommonRecordController
 * @description
 * @date 2020/7/28 13:26
 */
@Api(tags = "普通电子病历接口")
@RestController
@RequestMapping("/medical_common")
@CrossOrigin
public class MedicalCommonRecordController {

  @Autowired private MedicalCommonRecordBiz medicalCommonRecordBiz;

  /**
   * 新增普通电子病历
   *
   * @param model
   * @return
   */
  @PostMapping("/create")
  @ApiOperation("新增数据")
  public ResponseResult create(@RequestBody @Valid MedicalCommonRecordModel model) {
    return ResponseUtil.success(medicalCommonRecordBiz.create(model));
  }

}
