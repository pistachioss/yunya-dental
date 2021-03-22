package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author 杨柳絮 排班列表中排班信息
 * @className WorkDayVO
 * @description
 * @date 2020/7/24 15:30
 */
@Data
@ToString
public class WorkDayVO {
    //  @JsonFormat(shape=JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT+8")
//  private Date date;
//  private Integer compClinId;
//  /**
//   * 班次名
//   */
//  private String name;
//  /**
//   * 时间段
//   */
//  private String stime;
//  private Integer id;
//  /**
//   * 班次属性
//   */
//  private String type;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;
    @ApiModelProperty("门诊ID")
    private Integer compClinId;
    /**
     * 门诊名称
     */
    @ApiModelProperty("门诊名称")
    private String comName;
    /**
     * 班次名
     */
    @ApiModelProperty("班次名")
    private String name;

    /**
     * 时间段
     */
    @ApiModelProperty("时间段")
    private String stime;
    private Integer id;
    /**
     * 班次属性
     */
    @ApiModelProperty("班次属性")
    private String type;
}
