package com.yunya.employee.common.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author bruce
 * @date: 2020/7/10
 */
@Getter
@Setter
@ApiModel("分页对象")
public class PageReq
{
    @ApiModelProperty("当前页码")
    private int pageNum;
    @ApiModelProperty("每页条数")
    private int pageSize;
}
