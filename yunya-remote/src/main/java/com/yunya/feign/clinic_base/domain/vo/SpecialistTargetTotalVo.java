package com.yunya.feign.clinic_base.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@ApiModel(value = "公司查看总专科目标分页查询模型")
public class SpecialistTargetTotalVo {
    @ApiModelProperty(value = "主键id",required = true)
    private Integer id;
    @ApiModelProperty(value = "专科项目id",required = true)
    private Integer sspId;
    @ApiModelProperty(value = "目标",required = true)
    private BigDecimal target;
    @ApiModelProperty(value = "完成",required = true)
    private BigDecimal complete;
    @ApiModelProperty(value = "日期",required = true)
    private String date;
}
