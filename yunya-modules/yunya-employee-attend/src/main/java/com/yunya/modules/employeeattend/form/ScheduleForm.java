package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-07 13:18
 */
@Data
public class ScheduleForm implements Serializable {
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("类型名称")
    private String typeName;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;
}
