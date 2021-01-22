package com.yunya.feign.report.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 简介: 患者信息VO
 *
 * @author: chow
 * @date: 2021/1/22 14:55
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者信息VO")
public class PatientInfoVO {
  /** 主键 */
  @ApiModelProperty("患者ID")
  private Integer id;
  /** 患者姓名 字符串，长度64 */
  @ApiModelProperty("患者姓名")
  private String name;
  /** 患者头像url */
  @ApiModelProperty("患者头像路径")
  private String faceUrl;
  /** 手机号码 长度14 */
  @ApiModelProperty("手机号码")
  private String mobile;
  /** 病历号 患者第一次就诊时生成 */
  @ApiModelProperty("病历号")
  private String medicalNumber;
  /** 性别 0-男；1-女；2-未知 */
  @ApiModelProperty("性别 0-男；1-女；2-未知")
  private Byte gender;
  /** 年龄 */
  @ApiModelProperty("年龄")
  private Integer age;
  /** 末诊时间 */
  @ApiModelProperty("末诊时间")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date lastVisitTime;
  /** 末诊医生ID */
  @ApiModelProperty("末诊医生ID")
  private Integer dentistId;
  /** 末诊医生 */
  @ApiModelProperty("末诊医生")
  private String lastVisit;
}
