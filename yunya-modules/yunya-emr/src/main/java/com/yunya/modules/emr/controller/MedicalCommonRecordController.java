package com.yunya.modules.emr.controller;

import com.yunya.feign.emr.domain.model.MedicalCommonRecordModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.emr.biz.MedicalCommonRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

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
  @CurrentUser
  public ResponseResult create(@RequestBody @Valid MedicalCommonRecordModel model) {
    model.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    model.setCrtTime(new Date());
    return ResponseUtil.success(medicalCommonRecordBiz.create(model));
  }

}
