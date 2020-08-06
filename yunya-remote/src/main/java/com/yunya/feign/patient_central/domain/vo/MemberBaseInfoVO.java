package com.yunya.feign.patient_central.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/7/30 13:53
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class MemberBaseInfoVO implements Serializable {

    /**
     * 会员卡卡号
     */
    private String cardNumber;

    /**
     * 会员卡类型名称
     */
    private String memberCardName;

    /**
     * 会员本卡总余额（本金+赠金）
     */
    private BigDecimal memberCardMoneySum;

    /**
     * 会员卡类型id
     */
    private Integer memberTypeId;

    /**
     * 开卡日期
     */
    private Date crtTime;
}
