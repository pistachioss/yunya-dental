package com.yunya.feign.patient_central.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 主卡信息
 *
 * @author: WY
 * @date 2020/9/11 16:21
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class MasertMemberInfoVo implements Serializable {

    /**
     * 主卡人id
     */
    private Integer masterCardId;

    /**
     * 主卡人会员卡号
     */
    private String masterCardNumber;

    /**
     * 主卡人会员卡类型
     */
    private Integer masterCardTypeId;

    /**
     * 主卡人姓名
     */
    private String masterName;

    /**
     * 主卡人会员卡类型名称
     */
    private String masterMemberCardName;
}
