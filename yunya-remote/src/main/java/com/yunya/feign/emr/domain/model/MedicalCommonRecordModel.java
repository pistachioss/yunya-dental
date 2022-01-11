package com.yunya.feign.emr.domain.model;

import com.yunya.feign.emr.domain.vo.ExaminationsVO;
import com.yunya.feign.emr.domain.vo.MedicalGeneralNumVO;
import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author 杨柳絮
 * @className MedicalCommonRecordModel
 * @description 电子病历
 * @date 2020/7/29 13:24
 */
@Data
public class MedicalCommonRecordModel {

  @ApiModelProperty("ID")
  private Integer id;
  /**
   * 就诊ID
   */
  @ApiModelProperty("就诊ID")
  @NotNull(message = "就诊ID不能为空")
  private Integer treatmentId;

  /**
   * 就诊ID
   */
  @ApiModelProperty("就诊时间")
  private Date treatmentTime;

  /**
   * 患者ID
   */
  @ApiModelProperty("患者ID")
  @NotNull(message = "患者ID不能为空")
  private Integer patientId;

  @ApiModelProperty("审批ID")
  private Integer approvalId;

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

  @ApiModelProperty("审批操作截止时间，首次新增病历不传该字段 通过审批操作来添加病历 才传该字段")
  private String deadTime;

  @ApiModelProperty("创建病历员工姓名")
  private String majorDentistName;

  @ApiModelProperty("门诊名称")
  private String companyName;

  /** 牙位检查记录 */
  @ApiModelProperty("牙位检查记录")
  private List<MedicalCheckRecordModel> checkRecords;

  /** 照片影像id列表*/
  @ApiModelProperty("照片影像id列表")
  private List<Integer> xRayIds;

  /** 照片影像列表*/
  @ApiModelProperty("照片影像列表")
  private List<XUploadFileVO> xRayFilm;
}
