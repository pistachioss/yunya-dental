package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * 预约操作记录model
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 12:51
 * @update yunya-lihuibin    2020-08-10    新建
 */
@ApiModel(value = "预约操作记录model")
@Data
@ToString
public class AppointOperationModel implements Serializable {

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;

    /**
     * 预约ID
     */
    @ApiModelProperty(value = "预约ID")
    private Integer appointmentId;

    /**
     * 操作类型 操作记录(0-新建预约；1-修改预约；2-取消预约；3-确认预约；4；取消确认)
     */
    @ApiModelProperty(value = "操作类型 操作记录(0-新建预约；1-修改预约；2-取消预约；3-确认预约；4；取消确认)")
    private Byte operateType;

    /** 预约修改之前的内容 */
    @ApiModelProperty(value = "预约修改之前的内容")
    private String beforeOperation;

    /** 预约修改之后的内容 */
    @ApiModelProperty(value = "预约修改之后的内容")
    private String afterOperation;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remarks;

}
