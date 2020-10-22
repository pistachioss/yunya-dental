package com.yunya.feign.patient_central.domain.vo.web;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 简介: 会员卡结算余额查询
 *
 * @author: WY
 * @date: 2020/10/17 13:16
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class PatientMemberBalanceVo {

    /** 会员卡信息 */
    private MemberBaseInfoVo memberBaseInfoVo;

    /** 关联会员卡账户 */
    private List<MemberBaseInfoVo> memberBaseInfoVoList;
}