package com.yunya.modules.employeeattend.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author 杨柳絮
 * @className ClinicScheduleBaseVO
 * @description 开启该班次的门诊VO
 * @date 2020/8/3 9:23
 */
@Data
public class ClinicScheduleBaseVO {
  /**
   * 主键ID
   */
  @Id
  private Integer id;

  /**
   * 门诊ID
   */
  @ApiModelProperty("门诊ID")
  private Integer clinicId;

  /**
   * 排班ID
   */
  @ApiModelProperty("排班ID")
  private Integer scheduleId;

  /**
   * 是否启用
   */
  @ApiModelProperty("是否启用")
  private Boolean inservice;

  /**
   * 创建人ID
   */
  @ApiModelProperty("创建人ID")
  private Integer crtId;


  /**
   * 创建时间
   */
  @ApiModelProperty("创建时间")
  private Date crtTime;

  @ApiModelProperty("修改人ID")
  private Integer updId;

  /**
   * 修改时间
   */
  @ApiModelProperty("修改时间")
  private Date updTime;

  @ApiModelProperty("门诊名称")
  private String clinicName;
}
