package com.yunya.feign.wechat.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/4/25 11:22
 **/
@Data
public class WxAppointDetailVo {
    /**
     * 预约id
     */
    @ApiModelProperty(value = "预约id")
    private Integer id;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;

    /**
     * 患者名字
     */
    @ApiModelProperty(value = "患者名字")
    private String patientName;

    /**
     * 患者手机号
     */
    @ApiModelProperty(value = "患者手机号")
    private String patientMobile;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所名称")
    private String orgName;

    /**
     * 医生名字
     */
    @ApiModelProperty(value = "医生名字")
    private String dentistName;

    /**
     * 预约日期
     */
    @ApiModelProperty(value = "预约日期")
    private String appointDate;

    /**
     * 预约时间段 预约开始时间-预约结束时间
     */
    @ApiModelProperty(value = "预约时间段 预约开始时间-预约结束时间")
    private String appointPeriod;

    /**
     * 预约内容
     */
    @ApiModelProperty(value = "预约项目")
    private String appointContent;

    /**
     * 预约确认 0-未确认；1-确认
     */
    @ApiModelProperty(value = "预约确认 0-未确认；1-确认")
    private Boolean confirmStatus;

    /**
     * 预约状态 0-预约未到，1-履约，2，取消预约，3-失约
     */
    @ApiModelProperty(value = "预约状态 0-预约未到，1-履约，2，取消预约，3-失约")
    private Byte appointStatus;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;
}
