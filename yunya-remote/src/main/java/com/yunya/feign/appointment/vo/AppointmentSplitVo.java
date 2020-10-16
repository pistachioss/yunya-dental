package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 分解预约视图模型对象
 *
 * @author yunya-lihuibin
 * @create 2020-07-30 19:08
 * @update yunya-lihuibin    2020-07-30    新建
 */
@ApiModel(value = "分解预约视图模型对象")
@Data
@ToString
public class AppointmentSplitVo implements Serializable {
    /** 分解id */
    @ApiModelProperty(value = "分解id")
    private Integer id;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id")
    private Integer orgId;

    /** 预约id */
    @ApiModelProperty(value = "预约id")
    private Integer appointmentId;

    /** 分解开始时间 */
    @ApiModelProperty(value = "分解开始时间")
    private String splitStartTime;

    /** 分解结束时间 */
    @ApiModelProperty(value = "分解结束时间")
    private String splitEndTime;

    /** 医生id/助手id */
    @ApiModelProperty(value = "医生id/助手id")
    private Integer assistantId;

    /**
     * 医生id/助手id名字
     */
    @ApiModelProperty(value = "医生id/助手id名字")
    private String assistantName;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /** 是否启用 是否有效 */
    @ApiModelProperty(value = "是否启用 是否有效")
    private Boolean inservice;


}
