package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介: app端预约信息查询参数模型
 *
 * @author: chow
 * @date: 2020/10/12 14:12
 * @description:
 * @since: 1.0.0
 */
@ApiModel("app端预约信息查询参数模型")
@Data
@ToString
public class AppAppointmentInfoQuery implements Serializable {
  @ApiModelProperty("是否分页,默认true")
  private Boolean whetherPage = true;

  @ApiModelProperty("页码，默认第一页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示条数，默认10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 查询日期 */
  @ApiModelProperty(value = "查询日期", example = "yyyy-MM-dd")
  private String queryDate;
  /** 医生ID */
  @ApiModelProperty("预约医生ID")
  private Integer dentistId;
  /** 预约状态 */
  @ApiModelProperty("预约状态 0-预约未到，1-履约，2，取消预约，3-失约")
  private Byte appointStatus;
}
