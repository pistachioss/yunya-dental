package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简单介绍:</br> 医疗机构编辑参数封装Form
 *
 * @author: chow
 * @date: 2020/6/6 11:58
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "医疗机构信息编辑参数封装模型")
public class MedicalOrganizationInfoForm implements Serializable {
  /** 组织统一信用代码 */
  @ApiModelProperty(value = "组织统一信用代码",required = true)
  @Size(max = 32, message = "统一信用代码长度不能超过32个字符")
  @NotBlank(message = "统一信用代码为空")
  @Pattern(regexp = "^[0-9A-Z]{18}$", message = "统一信用代码格式出错")
  private String creditCode;
  /** 诊所电话 */
  @ApiModelProperty("诊所电话")
  @Size(max = 13, message = "电话长度不能超过13个字符")
  private String tel;
  /** 牙椅数量 */
  @ApiModelProperty("牙椅数量")
  private Integer chairQuantity;
  /** 营业开始时间 */
  @ApiModelProperty("营业开始时间")
  private String businessStartTime;
  /** 营业结束时间 */
  @ApiModelProperty("businessEndTime")
  private String businessEndTime;
  /** 地址省 */
  @ApiModelProperty("地址省")
  private String addrProvince;
  /** 地址市 */
  @ApiModelProperty("地址市")
  private String addrCity;
  /** 地址区 */
  @ApiModelProperty("地址区")
  private String addrRegion;
  /** 详细地址 */
  @ApiModelProperty("详细地址")
  private String address;
}
