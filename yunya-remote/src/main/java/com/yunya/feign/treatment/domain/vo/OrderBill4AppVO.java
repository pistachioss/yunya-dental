package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: App订单账单信息
 * @author: LHB
 * @create: 2020-12-16 16:44
 **/
@ApiModel(value = "OrderBill4AppVO",description = "App订单账单信息")
@Data
public class OrderBill4AppVO implements Serializable {
    @ApiModelProperty("账单ID")
    private Integer id;
    @ApiModelProperty("账单编号")
    private String billNumber;
    @ApiModelProperty("应收金额（消费总额）")
    private BigDecimal receivableAmount;
    @ApiModelProperty("本单优惠总额")
    private BigDecimal privilegeAmount;
    @ApiModelProperty("实际应收金额")
    private BigDecimal actualReceivableAmount;
    @ApiModelProperty("已收金额（本单收费总额）")
    private BigDecimal receivedAmount;
    @ApiModelProperty("欠费金额（本单欠费）")
    private BigDecimal debtAmount;
    @ApiModelProperty("开单项目列表")
    private List<TreatmentOrderItem4AppVO> billItems;



}
