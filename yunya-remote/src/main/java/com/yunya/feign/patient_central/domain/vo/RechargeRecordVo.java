package com.yunya.feign.patient_central.domain.vo;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简单介绍:</br> 会员卡充值记录Vo
 *
 * @author: WY
 * @date 2020/8/15 17:00
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class RechargeRecordVo implements Serializable {

    /**
     * 会员充值记录id
     */
    private Integer id;

    /**
     * 操作时间
     */
    private Date operatingTime;

    /**
     * 充值本金
     */
    private BigDecimal rechargePrincipal;

    /**
     * 充值赠金
     */
    private BigDecimal rechargeBonus;

    /**
     * 入账方式
     */
    private String payment;

    /**
     * 门诊id
     */
    private Integer orgId;

    /**
     * 诊所
     */
    private String orgName;

    /**
     * 操作人id
     */
    private Integer operatorId;

    /**
     * 操作人员
     */
    private String operatorName;

    /**
     * 备注
     */
    private String remarks;


}
