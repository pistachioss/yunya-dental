package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2022/11/15 11:25
 * @description: 患者会员权益数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者会员权益数据模型")
public class PatientVipRightInterestVO implements Serializable {

    /***************************最新激活产品信息***************************/
    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String couponName;

    /** 产品有效期 */
    @ApiModelProperty("产品有效期")
    private String useDeadline;

    /** 产品Logo图 */
    @ApiModelProperty("产品Logo图")
    private String couponLogo;

    /***************************会员卡信息***************************/
    /** 会员卡类型名称 */
    @ApiModelProperty("会员卡类型名称")
    private String memberCardName;

    /** 会员卡类型id */
    @ApiModelProperty("会员卡类型id")
    private Integer memberTypeId;

    /** 会员卡余额（含本金及赠金） */
    @ApiModelProperty("会员卡余额（含本金及赠金）")
    private BigDecimal memberCardMoneySum;

    /** 会员卡副卡人列表 */
    private List<PatientMemberRelationVo> memberRelationList;

    /** 会员卡余额共享人列表 */
    @ApiModelProperty("会员卡余额共享人列表")
    private List<PatientMemberRelationVo> memberBalanceRelationList;

    /***************************预付款信息***************************/
    /** 预付款余额（含本金赠金） */
    @ApiModelProperty("预付款余额（含本金及赠金）")
    private BigDecimal prepaymentMoneySum;
}
