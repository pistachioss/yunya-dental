package com.yunya.feign.system.vo;

import com.yunya.framework.common.annation.Excel;
import com.yunya.framework.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 用户（员工）信息
 *
 * @author: chow
 * @date: 2020/7/17 19:44
 * @description:
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class SysUserInfoDetail extends BaseEntity implements Serializable {
  /** 用户ID */
  @Excel(name = "用户ID")
  private Integer userId;

  /** 员工ID */
  private Integer employeeId;

  /** 用户名 */
  private String username;

  /** 真实姓名 */
  @Excel(name = "员工姓名")
  private String name;

  /** 姓名拼音 */
  private String pinyin;

  /** 生日 */
  @Excel(name = "出生日期")
  private String birthday;

  /** 地址 */
  private String address;

  /** 手机号码 */
  @Excel(name = "手机号码")
  private String mobilePhone;

  /** 邮箱地址 */
  private String email;

  /** 性别: 0, 男; 1, 女 */
  @Excel(name = "性别")
  private String genderValue;

  /** 性别0-男；1-女 */
  private Byte gender;

  /** 岗位ID */
  private String postIds;

  /** 员工岗位 */
  @Excel(name = "岗位")
  private String posts;

  /** 员工岗位组ID */
  private String groupIds;

  /** 员工岗位组 */
  private String postGroups;

  /** 组织ID */
  private String companyIds;

  /** 员工门诊 */
  @Excel(name = "可登陆门诊")
  private String companys;

  /** 描述 */
  private String description;

  /** 省份 */
  private String province;

  /** 城市 */
  private String city;

  /** 县/区 */
  private String country;

  /** 籍贯 */
  private String origin;

  /** 身份证号 */
  private String identity;

  /** 试用: 0, 正式: 1，实习: 2, 离职 3 */
  private Byte workStatus;

  /** 奖金系数 */
  private Double bonusCoefficient;

  /** 基础工作量 */
  private Double workAmount;

  /** 工作类型（兼职-0；全职-1） */
  private Byte workType;

  /** 合同签署日期 */
  private String contractSigningDate;

  /** 入职时间 */
  @Excel(name = "入职日期")
  private String entryDate;

  /** 毕业院校 */
  private String graduatedSchool;

  /** 学历 */
  private Integer education;

  /** 离职日期 */
  private String leaveTime;

  /** 职称 */
  private String title;

  /** 职级 */
  private String postLevel;

  /** 紧急联系人姓名 */
  private String emergencyContact;

  /** 紧急联系人联系人电话 */
  private String emergencyContactPhone;

  /** 是否有员工折扣 */
  private Boolean discount;

  /** 个人照片链接 */
  private String photo;

  /** 毕业证书照片:多张照片用;隔开 */
  private String diplomaPhoto;

  /** 工号 */
  private String workNumber;
}
