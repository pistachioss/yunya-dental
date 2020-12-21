package com.yunya.feign.clinic_base.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@ApiModel(value = "业务目标导出模型")
public class BusinessTargetExportQuery {

    @ApiModelProperty(value = "开始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "GMT+8")
    private Date startTime;
    @ApiModelProperty(value = "结束日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "GMT+8")
    private Date endTime;
    @ApiModelProperty(value = "组织类型 1组织 2 个人 公司传空")
    private Integer teamType;
    @ApiModelProperty(value = "对应id")
    private Integer numId;
}
