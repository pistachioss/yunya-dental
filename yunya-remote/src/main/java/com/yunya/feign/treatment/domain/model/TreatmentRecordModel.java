package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;

/**
 * 简介: 新增开始接诊参数模型
 *
 * @author: chow
 * @date: 2020/8/12 14:52
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开始接诊新增参数模型")
public class TreatmentRecordModel implements Serializable {
  /** 挂号ID */
  private Integer regId;
}
