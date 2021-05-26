package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介: 患者账单批量打印参数封装
 *
 * @author: chow
 * @date: 2021/5/19 15:17
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者账单批量打印参数封装")
@Data
@ToString
public class BillBatchPrintInfoQuery implements Serializable {
  /** 患者ID */
  @ApiModelProperty(value = "患者ID", required = true)
  @NotNull(message = "患者ID不能为空！")
  private Integer patientId;
  /** 账单ID列表 */
  @ApiModelProperty(value = "账单ID列表", required = true)
  @NotNull(message = "账单ID列表不能为空！")
  @Size(min = 1, message = "请至少选择一条账单记录！")
  private Integer[] billRecordIds;
}
