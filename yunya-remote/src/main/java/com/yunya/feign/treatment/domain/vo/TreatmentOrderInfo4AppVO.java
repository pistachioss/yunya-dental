package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: APP端，就诊详情-账单信息视图模型
 * @author: LHB
 * @create: 2020-12-17 20:18
 **/
@Data
@ApiModel(value = "TreatmentOrderInfo4AppVO",description = "APP端，就诊详情-账单信息视图模型")
public class TreatmentOrderInfo4AppVO implements Serializable {
    /** 账单编号 */
    @ApiModelProperty("账单编号")
    private String billNumber;
    @ApiModelProperty("开单项目")
    private List<TreatmentOrderItem4AppVO> orderItems;
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
}
