package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("账单异常处理VO")
@Data
@ToString
public class BillHandleRecordVO implements Serializable {
  /** 账单异常处理记录ID */
  @ApiModelProperty("账单异常处理记录ID")
  private Integer billHandleRecordId;
  /** 处理日期 */
  @ApiModelProperty("处理日期")
  private String handleDate;
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织名称 */
  @ApiModelProperty("组织名称")
  private String orgName;
  /** 操作人ID */
  @ApiModelProperty("操作人ID")
  private Integer operatorId;
  /** 操作人姓名 */
  @ApiModelProperty("操作人姓名")
  private String operatorName;
  /** 操作类型 */
  @ApiModelProperty("操作类型（0-收费方式调整；1-撤销收费；2-修改账单；3-账单退费）")
  private Byte operateType;
}
