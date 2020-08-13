package com.yunya.modules.treatment.controller;

import com.yunya.modules.treatment.biz.TreatmentRecordBiz;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 患者接诊管理控制器
 *
 * @author: chow
 * @date: 2020/8/12 14:48
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "患者接诊管理（开始接诊、就诊中列表查询）")
@RestController
@RequestMapping("admission")
public class TreatmentRecordController {
  /** 注入服务 */
  @Autowired private TreatmentRecordBiz treatmentRecordBiz;
}
