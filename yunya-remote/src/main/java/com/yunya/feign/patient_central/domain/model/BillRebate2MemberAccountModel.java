package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: chenlin
 * @date: 2023/7/13 10:16
 * @description: 账单返点添加模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单返点添加模型")
public class BillRebate2MemberAccountModel implements Serializable {

    /** 订单记录id */
    @ApiModelProperty(value = "订单记录id", required = true)
    @NotNull(message = "订单记录id不能为空")
    private Integer orderRecordId;

    /** 账单记录id */
    @ApiModelProperty(value = "账单记录id", required = true)
    @NotNull(message = "账单记录id不能为空")
    private Integer billRecordId;

    /** 收费记录id */
    @ApiModelProperty(value = "收费记录id", required = true)
    @NotNull(message = "收费记录id不能为空")
    private Integer billPayRecordId;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊id不能为空")
    private Integer orgId;

    /** 接收者-患者id */
    @ApiModelProperty(value = "接收者-患者id", required = true)
    @NotNull(message = "接收者-患者id不能为空")
    private Integer acceptorId;
    
    /** 返点类型：6-就诊账单返点，7-礼包账单返点 */
    @ApiModelProperty("返点类型：6-就诊账单返点，7-礼包账单返点")
    private Integer type = 7;

    /** 返点比例类型：0-不使用比例，直接返整；2-使用患者来源中赠金返点比例 */
    @ApiModelProperty(value = "返点比例类型：0-不使用比例，直接返整；2-使用患者来源中赠金返点比例", required = true)
    private Byte rebateRatioType = 2;

    /** 实收金额 */
    @ApiModelProperty(value = "实收金额", required = true)
    private BigDecimal receivedAmount = BigDecimal.ZERO;

}
