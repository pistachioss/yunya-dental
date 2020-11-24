package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: yunya-dental
 * @description: 账单明细打印列表
 * @author: LHB
 * @create: 2020-11-24 13:01
 **/
@ApiModel(value = "BillDetailPrintInfoVO",description = "账单明细打印列表")
@Data
public class BillDetailPrintInfoVO implements Serializable {
    @ApiModelProperty("牙位")
    private String toothBit;
    @ApiModelProperty("数量")
    private Integer quantity;
    @ApiModelProperty("治疗项目")
    private String name;
    @ApiModelProperty("项目英文名")
    private String englishName;
    @ApiModelProperty("项目单价")
    private BigDecimal price;
    @ApiModelProperty("项目原价")
    private BigDecimal receivableAmount;
    @ApiModelProperty("优惠金额")
    private BigDecimal privilegeAmount;
    @ApiModelProperty("应收金额")
    private BigDecimal actualReceivable;
    @ApiModelProperty("已收金额")
    private BigDecimal receivedAmount;
}
