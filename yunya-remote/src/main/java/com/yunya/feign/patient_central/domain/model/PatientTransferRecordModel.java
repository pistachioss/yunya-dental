package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: chenlin
 * @date: 2023/7/13 10:16
 * @description: 返点到赠金账户添加模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者转账添加模型")
public class PatientTransferRecordModel implements Serializable {

    /** 转账者卡号 */
    @ApiModelProperty(value = "转账者卡号", required = true)
    @NotEmpty(message = "转账者卡号不能为空")
    private String transferorNumber;

    /** 转账方式：0-预付款充值会员卡，1-预付款账户转账 */
    @ApiModelProperty(value = "转账方式：0-预付款充值会员卡，1-预付款账户转账", required = true)
    @NotNull(message = "转账方式不能为空")
    private Integer operateType;

    /** 接收者卡号 */
    @ApiModelProperty(value = "接收者卡号", required = true)
    @NotEmpty(message = "接收者卡号不能为空")
    private String acceptorNumber;

    /** 本金金额 */
    @ApiModelProperty(value = "本金金额")
    private BigDecimal principal;

    @ApiModelProperty(value = "赠金金额")
    private BigDecimal bonus;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;
}
