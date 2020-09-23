package com.yunya.feign.patient_central.domain.vo.web;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 预付款退费记录vo
 *
 * @author: WY
 * @date 2020/8/26 16:35
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PrepaidMeturnRecordVo implements Serializable {

    /**
     * 预付款退费记录id
     */
    private Integer id;

    /**
     * 操作时间
     */
    private String operatingTime;

    /**
     * 退本金金额
     */
    private BigDecimal returnRrincipalAmount;

    /**
     * 退赠送金额
     */
    private BigDecimal returnGiftAmount;

    /**
     * 退费方式
     */
    private Integer returnWayId;

    /**
     * 退费方式
     */
    private String returnWayType;

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
