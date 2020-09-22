package com.yunya.feign.patient_central.domain.vo.app;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: YK
 * @date: 2020/9/22 13:56
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "app端患者信息")
public class AppPatientBaseInfoVo {

  /** 患者id */
  private Integer id;

  /** 患者姓名 */
  private String name;

  /** 患者年龄 */
  private Integer age;

  /** 患者手机号 */
  private String mobile;

  /** 患者性别头像类型:012345 */
  private Integer patientKind;

  /** 患者性别 */
  private Byte gender;



}
