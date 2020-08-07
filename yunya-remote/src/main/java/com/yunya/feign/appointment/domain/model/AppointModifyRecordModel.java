package com.yunya.feign.appointment.domain.model;

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
 * 新增预约修改记录表单
 *
 * @author yunya-lihuibin
 * @create 2020-08-06 20:17
 * @update yunya-lihuibin    2020-08-06    新建
 */
@ApiModel(value = "新增预约修改记录表单")
@Data
@ToString
public class AppointModifyRecordModel implements Serializable {
    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID", required = true)
    @NotNull(message = "门诊id不能为空！")
    private Integer orgId;

    /**
     * 预约ID
     */
    @ApiModelProperty(value = "预约ID", required = true)
    @NotNull(message = "预约ID不能为空！")
    private Integer appointmentId;

    /**
     * 被修改的预约日期
     */
    @ApiModelProperty(value = "被修改的预约日期", required = true)
    @NotNull(message = "被修改的预约日期不能为空！")
    private Date appointDate;

    /**
     * 被修改的预约医生
     */
    @ApiModelProperty(value = "被修改的预约医生", required = true)
    @NotNull(message = "被修改的预约医生不能为空！")
    private Integer dentistId;
}
