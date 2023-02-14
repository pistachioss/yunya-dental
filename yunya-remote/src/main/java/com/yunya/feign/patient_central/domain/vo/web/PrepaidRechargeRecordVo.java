package com.yunya.feign.patient_central.domain.vo.web;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 简单介绍:</br> 返回预付款充值记录信息模型
 *
 * @author: WY
 * @date 2020/8/22 15:14
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回预付款充值记录信息模型")
public class PrepaidRechargeRecordVo extends SpecialPrepaidRechargeRecordVo {

    /**
     * 充值卡号
     */
    @Excel(name = "充值卡号", sort = 5)
    @ApiModelProperty("充值卡号")
    private String rechargeCardNumber;

    /**
     * 充值类型
     */
    @Excel(name = "充值类型", sort = 8, readConverterExp = "0=普通充值,1=充值卡充值")
    @ApiModelProperty("充值类型")
    private Integer rechargeType;
}
