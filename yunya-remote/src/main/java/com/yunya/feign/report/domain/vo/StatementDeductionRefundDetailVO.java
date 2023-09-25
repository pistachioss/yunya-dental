package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

@ApiModel("对账单划扣卡退费明细VO")
@Data
@ToString
public class StatementDeductionRefundDetailVO implements Serializable {
    /**
     * 售出日期
     */
    @Excel(name = "退费日期")
    @ApiModelProperty("退费日期")
    private String refundDate;

    @Excel(name = "账单编号")
    @ApiModelProperty("账单编号")
    private String billNumber;

    @Excel(name = "退款患者姓名")
    @ApiModelProperty("退款患者姓名")
    private String refundTargetName;
    /**
     * 售出对象手机号
     */
    @Excel(name = "退款患者手机号")
    @ApiModelProperty("退款患者手机号")
    private String refundTargetMobile;

    @Excel(name = "会员卡本金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("会员卡本金")
    private BigDecimal memberPrincipalAmount;
    @Excel(name = "会员卡赠金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("会员卡赠金")
    private BigDecimal memberBonusAmount;
    @Excel(name = "预付款本金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("预付款本金")
    private BigDecimal prepayPrincipalAmount;
    @Excel(name = "预付款赠金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("预付款预付款赠金")
    private BigDecimal prepayBonusAmount;
    /**
     * 支付宝
     */
    @Excel(name = "支付宝", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("支付宝")
    private BigDecimal aliPayAmount;
    /**
     * 微信
     */
    @Excel(name = "微信", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("微信")
    private BigDecimal weChatAmount;
    /**
     * 银行账户
     */
    @Excel(name = "银行账户", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("银行账户")
    private BigDecimal bankAmount;
    /**
     * 现金
     */
    @Excel(name = "现金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("现金")
    private BigDecimal cashAmount;

    @Excel(name = "杭州医保", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("杭州医保")
    private BigDecimal hzInsurance;
    /**
     * 售出人
     */
    @Excel(name = "退费人")
    @ApiModelProperty("退费人")
    private String refundOperatorName;
}
