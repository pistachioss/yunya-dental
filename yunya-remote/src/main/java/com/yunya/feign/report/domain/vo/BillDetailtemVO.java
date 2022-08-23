package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介: 开单项目数量及金额VO
 *
 * @author: chow
 * @date: 2020/12/7 10:40
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单项目数量VO")
@Data
@ToString
public class BillDetailtemVO implements Serializable {
  /** 项目ID */
  @ApiModelProperty("项目ID")
  private Integer itemId;

  /** 项目类型 */
  @ApiModelProperty("项目类型：0-价目表；1-商品")
  private Byte itemType;

  /** 门诊ID */
  @ApiModelProperty("门诊ID")
  private Integer orgId;

  /** 开单数量 */
  @ApiModelProperty("开单数量")
  private Integer quantity;

  /** 患者id */
  @ApiModelProperty("患者id")
  private Integer patientId;

  /** 开单日期*/
  @ApiModelProperty("开单日期")
  private Date orderDate;
}
