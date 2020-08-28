package com.yunya.feign.cash_balance.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@ApiModel(value = "专科设置分页查询模型")
public class SpecializedSubjectProjectQuery {

    //分页查询条件
    @ApiModelProperty(required = true)
    private Integer pageNum;
    @ApiModelProperty(required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

}

