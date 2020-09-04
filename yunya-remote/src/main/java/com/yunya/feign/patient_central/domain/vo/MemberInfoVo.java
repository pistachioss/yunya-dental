package com.yunya.feign.patient_central.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 会员卡信息Vo
 *
 * @author: WY
 * @date 2020/9/4 14:55
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class MemberInfoVo implements Serializable {

    /**
     * 门诊id
     */
    private Integer orgId;

    /**
     * 主卡人id
     */
    private Integer masterCardId;

    /**
     * 副卡人姓名
     */
    private String name;

    /**
     * 副卡人id
     */
    private Integer secondaryCardId;

    /**
     * 副卡人会员卡号
     */
    private String cardNumber;

    /**
     * 副卡人会员卡类型
     */
    private Integer memberTypeId;

    /**
     * 副卡人会员卡类型名称
     */
    private String memberCardName;
}
