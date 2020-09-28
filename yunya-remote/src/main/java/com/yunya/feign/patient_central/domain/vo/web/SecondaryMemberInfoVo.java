package com.yunya.feign.patient_central.domain.vo.web;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 副卡信息Vo
 *
 * @author: WY
 * @date 2020/9/4 14:55
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class SecondaryMemberInfoVo implements Serializable {

    /**
     * 副卡人会员信息表主键id
     */
    private Integer id;

    /**
     * 副卡人id
     */
    private Integer secondaryCardId;

    /**
     * 副卡人会员卡号
     */
    private String secondaryCardNumber;

    /**
     * 副卡人会员卡类型
     */
    private Integer secondaryMemberTypeId;

    /**
     * 副卡人姓名
     */
    private String secondaryName;

    /**
     * 副卡人会员卡类型名称
     */
    private String memberCardName;

    /**
     * 折扣率（价目表自动调价的折扣率）
     */
    private Float rate;
}
