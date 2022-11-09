package com.yunya.feign.emr.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author chenlin
 * @className MedicalCommonRecordModel
 * @description 普通电子病历数据模型
 * @date 2022-11-09 09:49
 */
@Data
@ToString
@ApiModel("普通电子病历数据模型")
public class MedicalCommonRecordVO implements Serializable {

  /** 病历id */
  @ApiModelProperty("ID")
  private Integer id;
  
  /** 就诊ID */
  @ApiModelProperty("就诊ID")
  private Integer treatmentId;

  /** 就诊日期 */
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @ApiModelProperty("就诊日期")
  private Date treatDate;

  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;

  /** 主治医生ID */
  @ApiModelProperty("主治医生ID")
  private Integer majorDentistId;

  /** 主诉 */
  @ApiModelProperty("主诉")
  private String chiefComplaint;

  /** 现病史 */
  @ApiModelProperty("现病史")
  private String presentIllness;

  /** 既往史 */
  @ApiModelProperty("既往史")
  private String pastHistory;

  /** 复诊 */
  @ApiModelProperty("复诊")
  private String reExamination;

  /** 检查-json字符串 */
  @ApiModelProperty("检查-json字符串")
  private String examination;

  /** 诊断 */
  @ApiModelProperty("诊断")
  private String diagnosis;

  /** 诊疗计划-json字符串 */
  @ApiModelProperty("诊疗计划-json字符串")
  private String plan;

  /** 诊疗方案-json字符串 */
  @ApiModelProperty("诊疗方案-json字符串")
  private String treatment;

  /** 处方 */
  @ApiModelProperty("处方")
  private String prescription;

  /** 类型：0初诊，1复诊 */
  @ApiModelProperty("类型：0初诊，1复诊")
  private Integer type;

  /** 创建者ID */
  @ApiModelProperty("创建者ID")
  private Integer crtId;

  /** 创建病历员工姓名 */
  @ApiModelProperty("创建病历员工姓名")
  private String crtName;

  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @ApiModelProperty("创建时间")
  private Date crtTime;

  /** 主治医生姓名 */
  @ApiModelProperty("主治医生姓名")
  private String majorDentistName;

  /** 照片影像列表*/
  @ApiModelProperty("照片影像列表")
  private List<XUploadFileVO> xrayFilms;
}
