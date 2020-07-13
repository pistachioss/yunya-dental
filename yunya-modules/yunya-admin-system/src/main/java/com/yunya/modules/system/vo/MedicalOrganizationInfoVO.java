package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: chow
 * @date: 2020/6/6 11:07
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class MedicalOrganizationInfoVO implements Serializable {
  /** 组织ID */
  private Integer companyId;
  /** 组织全名 */
  private String name;
  /** 组织信用代码 */
  private String creditCode;
  /** 品牌名称 */
  private String brandName;
  /** 门诊编号 */
  private String clinicNumber;
  /** 诊所简称 */
  private String abbreviation;
  /** 诊所电话 */
  private String tel;
  /** 诊所传真 */
  private String fax;
  /** 牙椅数量 */
  private Integer chairQuantity;
  /** 营业开始时间 */
  private String businessStartTime;
  /** 营业结束时间 */
  private String businessEndTime;
  /** 地址省 */
  private String addrProvince;
  /** 地址市 */
  private String addrCity;
  /** 地址区 */
  private String addrRegion;
  /** 详细地址 */
  private String address;
  /** 医疗机构图片地址 */
  private String path;
}
