package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
public class VacationSetQuery {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("启用状态 0否 1是")
    private Integer vacationEnable = 1;

    @ApiModelProperty("页数")
    private Integer page = 1;

    @ApiModelProperty("每页个数")
    private Integer size = 10;

    @ApiModelProperty("是否分页")
    private Boolean whetherPage = false;
}
