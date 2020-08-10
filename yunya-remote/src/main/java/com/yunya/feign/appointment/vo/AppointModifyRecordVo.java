package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 预约修改（医生和日期）视图模型
 *
 * @author yunya-lihuibin
 * @create 2020-08-08 16:42
 * @update yunya-lihuibin    2020-08-08    新建
 */
@ApiModel(value = "预约修改（医生和日期）视图模型")
@Data
@ToString
public class AppointModifyRecordVo implements Serializable {
    /**
     * 预约修改记录id
     */
    @ApiModelProperty(value = "预约修改记录id")
    private Integer id;

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
     * 被修改的预约日期
     */
    @ApiModelProperty(value = "被修改的预约日期")
    private Date appointDate;

    /**
     * 被修改的预约医生
     */
    @ApiModelProperty(value = "被修改的预约医生")
    private Integer dentistId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    @ApiModelProperty(value = "是否启用 是否有效")
    private Boolean inservice;


}
