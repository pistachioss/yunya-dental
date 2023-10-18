package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简单介绍:</br> 返回卡主信息和副卡人信息模型 主卡对副卡 一对多
 *
 * @author: WY
 * @date 2020/9/11 16:22
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
@ApiModel("返回卡主信息和副卡人信息模型")
public class MemberInfoVo implements Serializable {

    /**
     * 主卡人信息
     */
    private MasertMemberInfoVo masertMemberInfoVo;

    /**
     * 副卡人信息
     */
    private List<SecondaryMemberInfoVo> secondaryMemberInfoVos;

    /** 亲密付主卡人会员 */
    @ApiModelProperty("亲密付主卡人会员")
    private MasertMemberInfoVo intimatePayMember;

    /** 推荐关系人会员的次一级会员 */
    @ApiModelProperty("推荐关系人会员的次一级会员")
    private MasertMemberInfoVo recommendSecondMember;
}
