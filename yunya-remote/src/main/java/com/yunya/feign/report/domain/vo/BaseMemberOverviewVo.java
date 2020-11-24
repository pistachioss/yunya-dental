package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 简介: 会员概况
 *
 * @author: WY
 * @date: 2020/10/24 14:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("会员卡概况Vo")
public class BaseMemberOverviewVo {

    /** 会员卡级别id */
    @ApiModelProperty("会员卡级别id")
    private Integer memberLevelId;

    /** 会员名称 */
    @ApiModelProperty("会员名称")
    private String memberLevelName;

    /** 数量 */
    @ApiModelProperty("数量")
    private Integer amount;

    /** 剩余余额总额（含赠送金额） */
    @ApiModelProperty("剩余余额总额（含赠送金额）")
    private BigDecimal principalAmount;

    /** 剩余赠送金额总额 */
    @ApiModelProperty("剩余赠送金额总额")
    private BigDecimal bonusAmount;
    
}