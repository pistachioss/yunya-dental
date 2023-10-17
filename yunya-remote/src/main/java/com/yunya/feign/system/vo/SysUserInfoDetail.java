package com.yunya.feign.system.vo;

import com.yunya.framework.common.annation.Excel;
import com.yunya.framework.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 用户（员工）信息
 *
 * @author: chow
 * @date: 2020/7/17 19:44
 * @description:
 * @since: 1.0.0
 */
@ApiModel("用户（员工）详细信息")
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class SysUserInfoDetail extends BaseEntity implements Serializable {
  /** 用户ID */
  @Excel(name = "用户ID")
  @ApiModelProperty("用户ID")
  private Integer userId;
  /** 员工ID */
  @ApiModelProperty("员工ID")
  private Integer employeeId;
  /** 用户名 */
  @ApiModelProperty("用户名")
  private String username;
  /** 真实姓名 */
  @Excel(name = "员工姓名")
  @ApiModelProperty("真实姓名")
  private String name;
  /** 姓名拼音 */
  @ApiModelProperty("姓名拼音")
  private String pinyin;
  /** 生日 */
  @Excel(name = "出生日期")
  @ApiModelProperty("生日")
  private String birthday;
  /** 地址 */
  @ApiModelProperty("地址-家庭地址")
  private String address;
  /** 手机号码 */
  @Excel(name = "手机号码")
  @ApiModelProperty("手机号码")
  private String mobilePhone;
  /** 邮箱地址 */
  @ApiModelProperty("邮箱地址")
  private String email;
  /** 性别: 0, 男; 1, 女 */
  @ApiModelProperty("性别: 0, 男; 1, 女")
  private String genderValue;
  /** 性别0-男；1-女 */
  @Excel(name = "性别", readConverterExp = "0=男,1=女", type = Excel.Type.EXPORT)
  @ApiModelProperty("性别0-男；1-女")
  private Byte gender;
  /** 岗位ID */
  @ApiModelProperty("岗位ID")
  private String postIds;
  /** 员工岗位 */
  @Excel(name = "岗位")
  @ApiModelProperty("员工岗位")
  private String posts;
  /** 员工岗位组ID */
  @ApiModelProperty("员工岗位组ID")
  private String groupIds;
  /** 员工岗位组 */
  @ApiModelProperty("员工岗位组")
  private String postGroups;
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private String companyIds;
  /** 员工门诊 */
  @Excel(name = "可登陆门诊")
  @ApiModelProperty("员工门诊")
  private String companys;
  /** 描述 */
  @ApiModelProperty("描述")
  private String description;
  /** 省份 */
  @ApiModelProperty("省份")
  private String province;
  /** 城市 */
  @ApiModelProperty("城市")
  private String city;
  /** 县/区 */
  @ApiModelProperty("县、区")
  private String country;
  /** 籍贯 */
  @ApiModelProperty("籍贯")
  private String origin;
  /** 身份证号 */
  @ApiModelProperty("身份证号")
  private String identity;
  /** 试用: 0, 正式: 1，离职: 2 */
  @ApiModelProperty("试用: 0, 正式: 1，离职: 2")
  private Byte workStatus;
  /** 奖金系数 */
  @ApiModelProperty("奖金系数")
  private Double bonusCoefficient;
  /** 基础工作量 */
  @ApiModelProperty("基础工作量")
  private Double workAmount;
  /** 工作类型（兼职-0；全职-1） */
  @ApiModelProperty("工作类型（兼职-0；全职-1）")
  private Byte workType;
  /** 合同签署日期 */
  @ApiModelProperty("合同签署日期")
  private String contractSigningDate;
  /** 入职时间 */
  @Excel(name = "入职日期")
  @ApiModelProperty("入职时间")
  private String entryDate;
  /** 毕业院校 */
  @ApiModelProperty("毕业院校")
  private String graduatedSchool;
  /** 学历 */
  @ApiModelProperty("学历")
  private Integer education;
  /** 离职日期 */
  @ApiModelProperty("离职日期")
  private String leaveTime;
  /** 职称 */
  @ApiModelProperty("职称")
  private String title;
  /** 职级 */
  @ApiModelProperty("职级")
  private String postLevel;
  /** 紧急联系人姓名 */
  @ApiModelProperty("紧急联系人姓名")
  private String emergencyContact;
  /** 紧急联系人联系人电话 */
  @ApiModelProperty("紧急联系人联系人电话")
  private String emergencyContactPhone;
  /** 是否有员工折扣 */
  @ApiModelProperty("是否有员工折扣")
  private Boolean discount;
  /** 最大授权折扣率 */
  @ApiModelProperty("最大授权折扣率")
  private BigDecimal discountRate;
  /** 最大授权折扣金额 */
  @ApiModelProperty("最大授权折扣金额")
  private BigDecimal discountAmount;
  /** 个人照片链接 */
  @ApiModelProperty("个人照片链接")
  private String photo;
  /** 毕业证书照片:多张照片用;隔开 */
  @ApiModelProperty("毕业证书照片:多张照片用;隔开")
  private String diplomaPhoto;
  /** 工号 */
  @ApiModelProperty("工号")
  private String workNumber;
  /** 人员类别 */
  @ApiModelProperty("人员类别")
  private String category;
  /** 户口性质 */
  @ApiModelProperty("户口性质")
  private String registration;
  /** 户籍地址 */
  @ApiModelProperty("户籍地址")
  private String permanentAddress;
  /** 毕业时间 */
  @ApiModelProperty("毕业时间")
  private String graduationTime;
  /** 学习专业 */
  @ApiModelProperty("学习专业")
  private String studyMajor;
  /** 上一份工作 */
  @ApiModelProperty("上一份工作")
  private String lastJob;
  /** 试用期结束时间 */
  @ApiModelProperty("试用期结束时间")
  private String probationPeriodTime;
}
