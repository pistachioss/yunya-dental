package com.yunya.feign.patient_central.domain.vo.web;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简单介绍:</br> 消费记录Vo
 *
 * @author: WY
 * @date 2020/8/28 9:18
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class MemberExpendRecordVo implements Serializable {

    /**
     * 会员消费记录id
     */
    private Integer id;

    /**
     * 操作时间
     */
    private Date operatingTime;

    /**
     * 消费本金
     */
    private BigDecimal expendPrincipal;

    /**
     * 消费赠金
     */
    private BigDecimal expendGift;

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

    /**
     * 消费者
     */
    private String expendName;

}
