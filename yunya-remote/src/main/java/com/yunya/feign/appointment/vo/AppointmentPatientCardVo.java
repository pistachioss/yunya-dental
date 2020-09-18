package com.yunya.feign.appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 预约可视图患者卡片模型
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 20:13
 * @update yunya-lihuibin    2020-08-10    新建
 */
@ApiModel(value = "预约可视图患者卡片模型")
@Data
@ToString
public class AppointmentPatientCardVo implements Serializable {

    /** 预约id */
    @ApiModelProperty(value = "预约id")
    private Integer id;

    /** 患者id */
    @ApiModelProperty(value = "患者id")
    private Integer patientId;

    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String name;

    /** 患者年龄 */
    @ApiModelProperty(value = "患者年龄")
    private Integer age;

    /** 患者性别 0-男；1-女；2-未知 */
    @ApiModelProperty(value = "患者性别 0-男；1-女；2-未知 ")
    private Byte gender;

    /** 预约类型 0-初诊预约；1-复诊预约 */
    @ApiModelProperty(value = "预约类型 0-初诊预约；1-复诊预约")
    private Byte appointType;

    /** 预约内容 */
    @ApiModelProperty(value = "预约内容")
    private String appointContent;

    /** 预约日期 */
    @ApiModelProperty(value = "预约日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date appointDate;

    /** 预约时间段 */
    @ApiModelProperty(value = "预约时间段")
    private String appointTime;

    /** 预约时长 */
    @ApiModelProperty(value = "预约时长")
    private Integer appointDuration;


}
