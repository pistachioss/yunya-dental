package com.yunya.modules.system.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 简单介绍:</br> 用户参数封装模型
 *
 * @author: chow
 * @date: 2020/6/13 13:18
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("用户参数封装模型")
public class SysUserForm implements Serializable {
  /** 员工姓名 */
  @ApiModelProperty(value = "员工姓名", required = true)
  @NotBlank(message = "员工姓名不能为空！")
  @Size(max = 50, message = "员工姓名长度不能超过50个字符")
  private String name;
  /** 性别 */
  @ApiModelProperty(value = "性别", required = true)
  private Byte gender;
  /** 出生日期 */
  @ApiModelProperty(value = "出生日期", required = true)
  @NotNull(message = "出生日期不能为空！")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date birthday;
  /** 身份证号 */
  @ApiModelProperty(value = "身份证号", required = true)
  @NotBlank(message = "身份证号不能为空！")
  @Size(max = 18, message = "身份证号长度不能超过18个字符")
  private String identity;
  /** 用户名（手机号） */
  @ApiModelProperty(value = "用户名（手机号）", required = true)
  @NotBlank(message = "用户名（手机号）不能为空！")
  @Size(max = 13, message = "手机号长度不能超过13个字符")
  private String mobilePhone;
  /** 固定电话 */
  @ApiModelProperty(value = "固定电话")
  private String telephone;
  /** 就职状态 */
  @ApiModelProperty(value = "就职状态（0-试用期；1-已转正；2-离职）", required = true)
  private Byte workStatus;
  /** 奖金系数 */
  @ApiModelProperty("奖金系数")
  private Double bonusCoefficient;
  /** 基础工作量 */
  @ApiModelProperty("基础工作量")
  private Double workAmount;
  /** 全职/兼职 */
  @ApiModelProperty(value = "全职-1/兼职-0", required = true)
  private Byte workType;
  /** 合同签署日期 */
  @ApiModelProperty(value = "合同签署日期", required = true)
  @NotNull(message = "合同签署日期不能为空！")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date contractSigningDate;
  /** 入职日期 */
  @ApiModelProperty(value = "入职日期", required = true)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date entryDate;
  /** 离职日期 */
  @ApiModelProperty("离职日期（当就职状态为离职时传入）")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date leaveTime;
  /** 毕业院校 */
  @ApiModelProperty(value = "毕业院校", required = true)
  @NotBlank(message = "毕业院校不能为空！")
  @Size(max = 100, message = "毕业院校字段长度不能超过100个字符")
  private String graduatedSchool;
  /** 是否有授权折扣 */
  @ApiModelProperty(value = "是否有授权折扣", required = true)
  @NotNull(message = "是否有授权折扣不能为空！")
  private Boolean discount;
  /** 学历 */
  @ApiModelProperty(value = "学历", required = true)
  @NotNull(message = "学历不能为空！")
  private Integer education;
  /** 紧急联系人 */
  @ApiModelProperty("紧急联系人")
  @Size(max = 25, message = "紧急联系人姓名长度不能超过25个字符")
  private String emergencyContact;
  /** 紧急联系人电话 */
  @ApiModelProperty("紧急联系人电话")
  private String emergencyContactPhone;
  /** 现居地址 */
  @ApiModelProperty("居住地址")
  @Size(max = 100, message = "居住地址不能超过100个字符")
  private String address;
  /** 可登陆组织列表 */
  private List<LoginOrganizationForm> loginOrganizationForms;
}
