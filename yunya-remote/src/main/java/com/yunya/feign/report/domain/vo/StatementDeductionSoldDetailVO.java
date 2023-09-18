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

@ApiModel("对账单划扣卡售出明细VO")
@Data
@ToString
public class StatementDeductionSoldDetailVO implements Serializable {
    /**
     * 售出日期
     */
    @Excel(name = "收费日期")
    @ApiModelProperty("收费日期")
    private String soldDate;

    @Excel(name = "购卡患者姓名")
    @ApiModelProperty("购卡患者姓名")
    private String soldTargetName;
    /**
     * 售出对象手机号
     */
    @Excel(name = "购卡患者手机号")
    @ApiModelProperty("购卡患者手机号")
    private String soldTargetMobile;
    /**
     * 产品名称
     */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String productName;

    @Excel(name = "产品售出单价", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("产品售出单价")
    private BigDecimal soldAmount;
    /**
     * 产品类型
     */
    @Excel(name = "产品类型")
    @ApiModelProperty("产品类型:0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券")
    private Byte productType;
    /**
     * 卡ID
     */
    @ApiModelProperty("卡ID")
    private Integer cardId;
    /**
     * 卡号
     */
    @Excel(name = "卡号")
    @ApiModelProperty("卡号")
    private String cardNum;
    /**
     * 售出金额
     */

    /**
     * 现金
     */
    @Excel(name = "现金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("现金")
    private BigDecimal cashAmount;
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
     * 售出人ID
     */
    @ApiModelProperty("售出人ID")
    private Integer soldOperatorId;
    /**
     * 售出人
     */
    @Excel(name = "售出人")
    @ApiModelProperty("售出人")
    private String soldOperatorName;
}
