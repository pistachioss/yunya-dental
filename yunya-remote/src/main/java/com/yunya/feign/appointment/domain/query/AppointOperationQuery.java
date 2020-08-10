package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 查询预约操作记录参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 12:55
 * @update yunya-lihuibin    2020-08-10    新建
 */
@ApiModel(value = "查询预约操作记录参数封装")
@Data
@ToString
public class AppointOperationQuery implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

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

}
