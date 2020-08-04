package com.yunya.feign.emr.domain.form;

import com.yunya.feign.emr.domain.vo.ExaminationsVO;
import com.yunya.feign.emr.domain.vo.MedicalGeneralNumVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author 杨柳絮
 * @className MedicalCommonRecordForm
 * @description
 * @date 2020/8/3 12:53
 */
@Data
public class MedicalCommonRecordForm {
  @ApiModelProperty("ID")
  private Integer id;
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
  private List<ExaminationsVO> examination;

  /**
   * 诊断
   */
  @ApiModelProperty("诊断")
  private List<ExaminationsVO> diagnosis;

  /**
   * 诊疗计划
   */
  @ApiModelProperty("诊疗计划")
  private List<ExaminationsVO> plan;

  /**
   * 诊疗方案
   */
  @ApiModelProperty("诊疗方案")
  private List<ExaminationsVO> treatment;

  /**
   * 处方
   */
  @ApiModelProperty("处方")
  private String prescription;

  /**
   * 类型：0初诊，1复诊
   */
  @ApiModelProperty("类型：0初诊，1复诊")
  @NotNull(message = "类型不能为空")
  private Integer type;

  @ApiModelProperty("状态：0不需要审批，1待审核，2同意，3拒绝 新建病历时主治医生传0，助手传1")
  @NotNull(message = "状态不能为空")
  private Integer status;

  @ApiModelProperty("医生审批时间")
  private Date approvalTime;

  @ApiModelProperty("补写病例时间")
  private Date time;

  @ApiModelProperty("创建者ID")
  private Integer crtId;

  @ApiModelProperty("创建时间")
  private Date crtTime;

  @ApiModelProperty("修改者ID")
  private Integer updId;

  @ApiModelProperty("修改时间")
  private Date updTime;

  @ApiModelProperty("词条使用频率")
  private List<MedicalGeneralNumVO>medicalGeneralNumList;

}
