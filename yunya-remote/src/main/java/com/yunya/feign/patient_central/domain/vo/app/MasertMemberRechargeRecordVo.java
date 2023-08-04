package com.yunya.feign.patient_central.domain.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/14
 * @description:
 */
@ToString
@Data
@ApiModel("小程序返回消费信息参数模型")
public class MasertMemberRechargeRecordVo {

    @ApiModelProperty(value = "会员消费记录列表")
    private List<MasertMemberRechargeRecordDetailVo> list;
    @ApiModelProperty(value = "预付款消费记录列表")
    private List<MasertMemberRechargeRecordDetailVo> prelist;

    /**
     * 会员本卡总余额（本金+赠金）
     */
    @ApiModelProperty(value = "会员本卡总余额（本金+赠金）")
    private BigDecimal memberCardMoneySum;
    
    /** 会员卡赠金 */
    @ApiModelProperty("会员卡赠金")
    private BigDecimal memberBouns;

    /**
     * 预付款总余额（本金+赠金）
     */
    @ApiModelProperty(value = "预付款总余额（本金+赠金）")
    private BigDecimal prepaymentsMoneySum;


}
