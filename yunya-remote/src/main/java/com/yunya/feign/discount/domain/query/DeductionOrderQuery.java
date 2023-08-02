package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author xiangyang
 * @date 2023/7/17
 */
@Getter
@Setter
@ApiModel(value = "患者档案划扣卡订单列表")
public class DeductionOrderQuery {
    @ApiModelProperty(value = "购买门诊")
    private List<Integer> orgList;
    @ApiModelProperty(value = "开始日期")
    private LocalDate soldStartDate;
    @ApiModelProperty(value = "结束日期")
    private LocalDate soldEndDate;
    @ApiModelProperty(value = "患者id",required = true)
    @NotNull
    private Integer patientId;
    @ApiModelProperty(value = "页码", required = true)
    @NotNull
    private Integer pageNum;
    @ApiModelProperty(value = "每页数量", required = true)
    @NotNull
    private Integer pageSize;
}
