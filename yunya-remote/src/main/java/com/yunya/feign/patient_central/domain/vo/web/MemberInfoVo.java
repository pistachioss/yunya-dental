package com.yunya.feign.patient_central.domain.vo.web;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简单介绍:</br> 主卡对副卡 一对多
 *
 * @author: WY
 * @date 2020/9/11 16:22
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class MemberInfoVo implements Serializable {

    /**
     * 主卡人信息
     */
    private MasertMemberInfoVo masertMemberInfoVo;

    /**
     * 副卡人信息
     */
    private List<SecondaryMemberInfoVo> secondaryMemberInfoVos;
}
