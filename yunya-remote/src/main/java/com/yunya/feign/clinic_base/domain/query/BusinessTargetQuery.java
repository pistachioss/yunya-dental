package com.yunya.feign.clinic_base.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@ApiModel(value = "业务目标分页查询模型")
public class BusinessTargetQuery {

    @ApiModelProperty(value = "日期类型 0月 1年",required = true)
    private Integer dateType;
    @ApiModelProperty(value = "团队类型 0个人 1门诊",required = true)
    private Integer teamType;
    @ApiModelProperty(value = "团队类型id",required = true)
    private Integer numId;
    //根据日期查询分页
    @ApiModelProperty(value = "开始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "GMT+8")
    private Date startTime;
    @ApiModelProperty(value = "结束日期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "GMT+8")
    private Date endTime;

    //分页查询条件
    @ApiModelProperty(required = true)
    private Integer pageNum;
    @ApiModelProperty(required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;


}
