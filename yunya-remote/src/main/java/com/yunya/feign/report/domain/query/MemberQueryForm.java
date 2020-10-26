package com.yunya.feign.report.domain.query;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介: 会员卡Occur日志QueryForm
 *
 * @author: WY
 * @date: 2020/10/24 13:49
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class MemberQueryForm implements Serializable {

    /** 门诊id */
    private Integer orgId;

    /** 患者条件 */
    private String combination;

    /** 会员卡号 */
    private String cardNumber;

    /** 充值开始日期 */
    private Date startDate;

    /** 充值开始日期 */
    private Date endDate;

    /** 会员卡名称 */
    private Integer cardName;

}