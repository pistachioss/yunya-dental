package com.yunya.feign.appointment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改预约状态表单
 *
 * @author yunya-lihuibin
 * @create 2020-08-08 16:20
 * @update yunya-lihuibin    2020-08-08    新建
 */
@ApiModel(value = "修改预约状态表单")
@Data
@ToString
public class AppointStatusForm implements Serializable {
    /** 预约状态 0-预约未到，1-履约，2，取消预约，3-失约 */
    @ApiModelProperty(value = "预约状态 0-预约未到，1-履约，2，取消预约，3-失约", required = true)
    @NotNull(message = "预约状态不能为空！")
    private Byte appointStatus;

    /** 修改备注 */
    @ApiModelProperty(value = "修改备注")
    private String remarks;
}
