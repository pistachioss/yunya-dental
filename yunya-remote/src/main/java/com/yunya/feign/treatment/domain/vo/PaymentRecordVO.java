package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: yunya-dental
 * @description: 会员卡信息视图模型(患者档案-就诊记录-账单详情-收费信息-预付款/会员卡
 * @author: LHB
 * @create: 2020-11-10 19:24
 **/
@Data
@ApiModel(value = "MemberAccountVO",description = "会员卡信息视图模型(患者档案-就诊记录-账单详情-收费信息-预付款/会员卡)")
public class PaymentRecordVO implements Serializable {
    @ApiModelProperty(value = "卡号")
    private String cardNumber;
    @ApiModelProperty(value = "会员卡本金 充值金额")
    private BigDecimal principalAmount;
    @ApiModelProperty(value = "会员卡赠金 充值赠送金额")
    private BigDecimal bonusAmount;
}
