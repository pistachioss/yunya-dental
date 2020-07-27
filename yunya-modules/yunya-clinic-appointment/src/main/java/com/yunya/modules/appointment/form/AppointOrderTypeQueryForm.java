package com.yunya.modules.appointment.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 预约项目查询参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-07-27 15:12
 * @update yunya-lihuibin    2020-07-27    新建
 */
@ApiModel("预约项目查询参数封装")
@Data
public class AppointOrderTypeQueryForm implements Serializable {
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    @ApiModelProperty("预约项目id")
    private Integer orderId;

    @ApiModelProperty("预约名称")
    private String name;

}
