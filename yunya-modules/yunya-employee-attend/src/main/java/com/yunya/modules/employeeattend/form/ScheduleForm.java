package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
}
