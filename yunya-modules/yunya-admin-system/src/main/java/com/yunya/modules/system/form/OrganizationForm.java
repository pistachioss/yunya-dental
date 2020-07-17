package com.yunya.modules.system.form;

import com.yunya.models.system.Company;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简单介绍:</br> 组织信息参数封装类
 *
 * @author: chow
 * @date: 2020/6/1 09:25
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@ApiModel("组织信息修改参数模型")
public class OrganizationForm extends Company implements Serializable {
  /** 门诊编号 */
  @ApiModelProperty(value = "医疗机构编号", example = "0001")
  @Size(max = 4, message = "门诊编号长度不能超过4位")
  private String clinicNumber;
  /** 门诊简称 */
  @ApiModelProperty(value = "医疗机构简称", example = "艾维口腔")
  @Size(max = 50, message = "门诊简称不能超过50个字符")
  private String abbreviation;
  /** 门诊品牌 */
  @ApiModelProperty(value = "医疗机构品牌id，可多个", example = "[54,55]")
  private Byte[] brandIds;
}
