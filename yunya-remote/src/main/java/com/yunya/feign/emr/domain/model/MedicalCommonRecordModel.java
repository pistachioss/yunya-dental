package com.yunya.feign.emr.domain.model;

import com.yunya.feign.emr.domain.vo.ExaminationsVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

import java.util.List;

/**
 * @author 杨柳絮
 * @className MedicalCommonRecordModel
 * @description 电子病历
 * @date 2020/7/29 13:24
 */
@Data
public class MedicalCommonRecordModel {

  /**
   * 就诊ID
   */
  @ApiModelProperty("就诊ID")
  @NotNull(message = "就诊ID不能为空")
  private Integer treatmentId;

  /**
   * 患者ID
   */
  @ApiModelProperty("患者ID")
  @NotNull(message = "患者ID不能为空")
  private Integer patientId;

  /**
   * 主治医生ID
   */
  @ApiModelProperty("主治医生ID")
  @NotNull(message = "主治医生ID不能为空")
  private Integer majorDentistId;

  /**
   * 主诉
   */
  @ApiModelProperty("主诉")
  private String chiefComplaint;

  /**
   * 现病史
   */
  @ApiModelProperty("现病史")
  private String presentIllness;

  /**
   * 既往史
   */
  @ApiModelProperty("既往史")
  private String pastHistory;

  /**
   * 复诊
   */
  @ApiModelProperty("复诊")
  private String reExamination;

  /**
   * 检查
   */
  @ApiModelProperty("检查")
  private List<ExaminationsVO> examinations;

  /**
   * 诊断
   */
  @ApiModelProperty("诊断")
  private List<ExaminationsVO> diagnosiss;

  /**
   * 诊疗计划
   */
  @ApiModelProperty("诊疗计划")
  private List<ExaminationsVO> plans;

  /**
   * 诊疗方案
   */
  @ApiModelProperty("诊疗方案")
  private List<ExaminationsVO> treatments;

  /**
   * 处方
   */
  @ApiModelProperty("处方")
  private String prescription;

  /**
   * 类型：0初诊，1复诊
   */
  @ApiModelProperty("类型：0初诊，1复诊")
  private Boolean type;

}
