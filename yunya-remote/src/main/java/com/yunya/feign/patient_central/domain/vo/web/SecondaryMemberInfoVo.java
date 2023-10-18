package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

/**
 * 简单介绍:</br> 返回副卡人信息模型
 *
 * @author: WY
 * @date 2020/9/4 14:55
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
@ApiModel("返回副卡人信息模型")
public class SecondaryMemberInfoVo extends MemberTypeBaseVO {

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
     * 副卡人姓名
     */
    private String secondaryName;
}
