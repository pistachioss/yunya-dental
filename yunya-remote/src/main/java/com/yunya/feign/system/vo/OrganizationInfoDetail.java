package com.yunya.feign.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 组织详情VO
 *
 * @author: chow
 * @date: 2020/6/4 17:03
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class OrganizationInfoDetail implements Serializable {
  /** 组织ID */
  private Integer id;
  /** 组织父ID */
  private Integer parentId;
  /** 组织全名 */
  private String name;
  /** 组织统一信用代码 */
  private String creditCode;
  /** 组织类型 */
  private String type;
  /** 自定义组织排序 */
  private Integer orderNum;
  /** 组织编号 */
  private String clinicNumber;
  /** 组织简称 */
  private String abbreviation;
  /** 组织品牌 */
  private String brandName;
  /**
   * 电话
   */
  private String tel;
  /**
   * 地址
   */
  private String address;
  /** 营业开始时间*/
  @ApiModelProperty("营业开始时间")
  private String businessStartTime;
  /** 营业结束时间*/
  @ApiModelProperty("营业结束时间")
  private String businessEndTime;
}
