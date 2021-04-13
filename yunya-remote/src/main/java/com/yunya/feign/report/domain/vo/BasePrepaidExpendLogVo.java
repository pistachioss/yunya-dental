package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 简介: 预付款消费列表Vo
 *
 * @author: WY
 * @date: 2020/10/24 14:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("预付款消费列表Vo")
public class BasePrepaidExpendLogVo {

    /** 操作id **/
    @ApiModelProperty("操作id")
    private Integer occurLogId;

    /** 消费日期 */
    @Excel(name = "消费日期")
    @ApiModelProperty("消费日期")
    private String occurDate;

    /** 账单日期 */
    @Excel(name = "账单日期")
    @ApiModelProperty("账单日期")
    private String orderDate;

    /** 账单编号 */
    @Excel(name = "账单编号")
    @ApiModelProperty("账单编号")
    private String orderNum;

    /** 患者 */
    @Excel(name = "患者")
    @ApiModelProperty("患者")
    private String name;

    /** 手机号 */
    @Excel(name = "手机号")
    @ApiModelProperty("手机号")
    private String mobile;

    /** 预付款账户户主 */
    @Excel(name = "预付款账户户主")
    @ApiModelProperty("预付款账户户主")
    private String masterCardName;

    /** 预付款账号 */
    @Excel(name = "预付款账号")
    @ApiModelProperty("预付款账号")
    private String cardNumber;

    /** 本次消费本金 */
    @Excel(name = "本次消费本金", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("本次消费本金")
    private BigDecimal principalAmount = new BigDecimal(0);

    /** 本次消费赠金 */
    @Excel(name = "本次消费赠金", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("本次消费赠金")
    private BigDecimal bonusAmount = new BigDecimal(0);

    /** 消费后预付款余额（含赠送金额） */
    @Excel(name = "消费后预付款余额（含赠送金额）", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("消费后预付款余额（含赠送金额）")
    private BigDecimal currentExpendPrincipal = new BigDecimal(0);

    /** 剩余预付款余额（含赠送金额） */
    @Excel(name = "剩余预付款余额（含赠送金额）", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("剩余预付款余额（含赠送金额）")
    private BigDecimal currentAmount = new BigDecimal(0);

    /** 收费人 */
    @ApiModelProperty("收费人")
    @Excel(name = "收费人")
    private String operatorUserName;

}