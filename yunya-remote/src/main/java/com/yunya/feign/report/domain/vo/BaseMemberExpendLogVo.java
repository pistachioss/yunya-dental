package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 简介: 报表会员卡充值vo
 *
 * @author: WY
 * @date: 2020/10/24 14:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("会员消费列表Vo")
public class BaseMemberExpendLogVo {

    /** 操作id **/
    @ApiModelProperty(value = "操作id")
    private Integer occurLogId;

    /** 消费日期 */
    @Excel(name = "消费日期")
    @ApiModelProperty(value = "消费日期")
    private String occurDate;

    /** 账单日期 */
    @Excel(name = "账单日期")
    @ApiModelProperty(value = "账单日期")
    private String orderDate;

    /** 订单号 */
    @Excel(name = "账单编号")
    @ApiModelProperty(value = "账单编号")
    private String orderNum;

    /** 患者姓名 */
    @Excel(name = "患者")
    @ApiModelProperty(value = "患者")
    private String name;

    /** 手机号 */
    @Excel(name = "手机号")
    @ApiModelProperty(value = "手机号")
    private String mobile;

    /** 主卡人姓名 */
    @Excel(name = "主卡人")
    @ApiModelProperty(value = "主卡人")
    private String masterCardName;

    /** 会员卡号 */
    @Excel(name = "会员卡号")
    @ApiModelProperty(value = "会员卡号")
    private String cardNumber;

    /** 会员级别id */
    @ApiModelProperty(value = "会员级别id")
    private Integer memberLevelId;

    /** 会员卡级别名称 */
    @Excel(name = "会员卡名称")
    @ApiModelProperty(value = "会员卡名称")
    private String memberLevelName;

    /** 消费本金金额 */
    @Excel(name = "本次消费本金")
    @ApiModelProperty(value = "本次消费本金")
    private BigDecimal principalAmount = new BigDecimal(0);

    /** 消费赠金金额 */
    @Excel(name = "本次消费赠金")
    @ApiModelProperty(value = "本次消费赠金")
    private BigDecimal bonusAmount = new BigDecimal(0);

    /** 消费后会员卡余额（本金） */
    @Excel(name = "消费后会员卡余额（含赠送金额）")
    @ApiModelProperty(value = "消费后会员卡余额（含赠送金额）")
    private BigDecimal currentExpendPrincipal = new BigDecimal(0);

    /** 剩余会员卡余额（含赠送金额） */
    @Excel(name = "剩余会员卡余额（含赠送金额）")
    @ApiModelProperty(value = "剩余会员卡余额（含赠送金额）")
    private BigDecimal currentAmount = new BigDecimal(0);

    /** 消费人 */
    @Excel(name = "收费人")
    @ApiModelProperty(value = "收费人")
    private String operatorUserName;
}