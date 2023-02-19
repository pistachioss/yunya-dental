package com.yunya.feign.patient_central.domain.vo.web;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 专项预付款充值记录信息模型
 *
 * @author: WY
 * @date 2020/8/22 15:14
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("专项预付款充值记录信息模型")
public class SpecialPrepaidRechargeRecordVo implements Serializable {
    /**
     * 会员充值记录id
     */
    @ApiModelProperty("会员充值记录id")
    private Integer id;

    /**
     * 操作时间
     */
    @Excel(name = "操作时间", sort = 1)
    @ApiModelProperty("操作时间")
    private String operatingTime;

    /**
     * 充值金额
     */
    @Excel(name = "充值金额", sort = 2)
    @ApiModelProperty("充值金额")
    private BigDecimal rechargePrincipal;

    /**
     * 赠送金额
     */
    @Excel(name = "赠送金额", sort = 3)
    @ApiModelProperty("赠送金额")
    private BigDecimal rechargeBonus;

    /**
     * 入账方式id
     */
    @ApiModelProperty("入账方式id")
    private Integer paymentId;

    /**
     * 入账方式
     */
    @Excel(name = "入账方式", sort = 4)
    @ApiModelProperty("入账方式")
    private String paymentName;


    /**
     * 门诊id
     */
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /**
     * 诊所
     */
    @Excel(name = "诊所", sort = 6)
    @ApiModelProperty("诊所")
    private String orgName;


    /**
     * 操作人id
     */
    @ApiModelProperty("操作人id")
    private Integer operatorId;

    /**
     * 操作人员
     */
    @Excel(name = "操作人员", sort = 7)
    @ApiModelProperty("操作人员")
    private String operatorName;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remarks;

}
