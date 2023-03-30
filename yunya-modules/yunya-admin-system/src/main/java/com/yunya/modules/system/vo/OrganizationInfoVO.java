package com.yunya.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.List;

/**
 * 简单介绍:</br> 组织详情VO
 *
 * @author: chow
 * @date: 2020/6/4 17:03
 * @description:
 * @since: 1.0.0
 */
@ApiModel("组织详情VO")
@Data
@ToString
public class OrganizationInfoVO implements Serializable {
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer id;
  /** 组织父ID */
  @ApiModelProperty("组织父ID")
  private Integer parentId;
  /** 组织全名 */
  @ApiModelProperty("组织全名")
  private String name;
  /** 组织统一信用代码 */
  @ApiModelProperty("组织统一信用代码")
  private String creditCode;
  /** 组织类型 */
  @ApiModelProperty("组织类型")
  private String type;
  @ApiModelProperty("门诊图片")
  private String path;
  /** 自定义组织排序 */
  @ApiModelProperty("自定义组织排序")
  private Integer orderNum;
  /** 组织编号 */
  @ApiModelProperty("组织编号")
  private String clinicNumber;
  /** 组织简称 */
  @ApiModelProperty("组织简称")
  private String abbreviation;
  /** 组织品牌 */
  @ApiModelProperty("组织品牌")
  private String brandName;
  /**电话*/
  @ApiModelProperty("电话")
  private String tel;
  /** 地址 */
  @ApiModelProperty("地址")
  private String address;
  /** 营业开始时间*/
  @ApiModelProperty("营业开始时间")
  private String businessStartTime;
  /** 营业结束时间*/
  @ApiModelProperty("营业结束时间")
  private String businessEndTime;
  /** 门诊图片完整地址 */
  @ApiModelProperty("门诊图片完整地址")
  private String clinicPath;
  /** 距离门诊km */
  @ApiModelProperty("距离门诊km")
  private Double distance;

  /** 子门诊ID列表*/
  @ApiModelProperty("子门诊ID列表")
  private List<Integer> orgIds;
  private Boolean enableQztSync;
  private String qztInstitutionCode;
  private Boolean enableBjSync;
  private String bjInstitutionCode;
  private Boolean enableXhqSync;
  private String xhqInstitutionCode;
}
