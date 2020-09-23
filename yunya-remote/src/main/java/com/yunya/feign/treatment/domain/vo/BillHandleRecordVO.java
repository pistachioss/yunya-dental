package com.yunya.feign.treatment.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 账单异常处理VO
 *
 * @author: chow
 * @date: 2020/9/12 14:52
 * @description:
 * @since: 1.0.0i
 */
@Data
@ToString
public class BillHandleRecordVO implements Serializable {
  /** 账单异常处理记录ID */
  private Integer billHandleRecordId;
  /** 处理日期 */
  private String handleDate;
  /** 组织ID */
  private Integer orgId;
  /** 组织名称 */
  private String orgName;
  /**操作人ID*/
  private Integer operatorId;
  /** 操作人姓名 */
  private String operatorName;
  /** 操作类型 */
  private Byte operateType;
}
