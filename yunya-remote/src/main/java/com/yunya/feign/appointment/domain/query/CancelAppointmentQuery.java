package com.yunya.feign.appointment.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 简介：取消预约查询
 *
 * @author: chenlin
 * @Description: 取消预约查询
 * @Date: 2021/4/27 15:53
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("取消预约查询")
@ToString
@Data
public class CancelAppointmentQuery extends PageQuery implements Serializable {
    /** 查询开始日期*/
    @ApiModelProperty("查询开始日期")
    @NotEmpty(message = "查询开始日期不能为空！")
    private String startDate;

    /** 查询结束日期*/
    @ApiModelProperty("查询结束日期")
    @NotEmpty(message = "查询结束日期不能为空！")
    private String endDate;

    /** 门诊ID列表*/
    @ApiModelProperty("门诊ID列表")
    private Integer[] orgIds;

    /** 预约医生列表*/
    @ApiModelProperty("预约医生列表")
    private Integer[] dentistIds;
}
