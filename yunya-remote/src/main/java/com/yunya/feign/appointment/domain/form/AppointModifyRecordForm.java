package com.yunya.feign.appointment.domain.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 预约修改记录
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 10:31
 * @update yunya-lihuibin    2020-08-10    新建
 */
@ApiModel(value = "预约修改记录")
@Data
@ToString
public class AppointModifyRecordForm implements Serializable {
    /**
     * 预约修改记录id
     */
    @ApiModelProperty(value = "预约修改记录id", required = true)
    @NotNull(message = "预约修改记录id不能为空！")
    private Integer id;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID",required = true)
    @NotNull(message = "诊所ID不能为空！")
    private Integer orgId;

    /**
     * 预约ID
     */
    @ApiModelProperty(value = "预约ID",required = true)
    @NotNull(message = "预约ID不能为空！")
    private Integer appointmentId;

    /**
     * 被修改的预约日期
     */
    @ApiModelProperty(value = "被修改的预约日期", required = true)
    @NotNull(message = "被修改的预约日期不能为空！")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date appointDate;

    /**
     * 被修改的预约医生
     */
    @ApiModelProperty(value = "被修改的预约医生", required = true)
    @NotNull(message = "被修改的预约医生不能为空！")
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
