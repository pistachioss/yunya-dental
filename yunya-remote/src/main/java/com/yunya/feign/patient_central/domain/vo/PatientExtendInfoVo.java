package com.yunya.feign.patient_central.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 新增患者基本信息+扩展信息VO
 *
 * @author: WY
 * @date 2020/7/28 11:13
 * @description: 患者基本信息表 扩展表 患者其他信息表
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientExtendInfoVo implements Serializable {

  /*************** 基础信息 **************/
  /** 患者ID */
  private Integer id;

  /** 诊所ID 添加患者的组织ID */
  private Integer orgId;

  /** 患者姓名 字符串，长度64 */
  private String name;

  /** 拼音姓名 字符串，长度64 */
  private String pinyinName;

  /** 手机号码 长度14 */
  private String mobile;

  /** 病历号 患者第一次就诊时生成 */
  private String medicalNumber;

  /** 性别 0-男；1-女；2-未知 */
  private Byte gender;

  /** 年龄 */
  private Integer age;

  /** 出生日期 */
  private String birthday;

  /** 备注 备注 */
  private String remarks;

  /*************** 扩展信息 *****************/
  /** 患者类型 患者类型对应字典ID */
  private Integer patientKind;

  /** 常用电话 常用电话 */
  private String usefulPhone;

  /** 身份证号 */
  private String identity;

  /** 职业字典明细ID 职业对应字典ID */
  private Integer profession;

  /** 遗传病史 */
  private String heredity;

  /** 其他健康情况 */
  private String otherHealth;

  /************** 患者其他信息表 **************/
  /** 过敏原 */
  private String allergens;
  /** 疾病史 */
  private String diseases;
  /** 标签 */
  private String labels;

  /******************* 会员卡 ******************/
  /** 会员卡ID */
  private Integer memberTypeId;
}
