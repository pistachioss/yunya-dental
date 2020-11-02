package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 简介: 返回会员卡结算余额信息模型
 *
 * @author: WY
 * @date: 2020/10/17 13:16
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
@ApiModel("返回会员卡结算余额信息模型")
public class PatientMemberBalanceVo {

    /** 会员卡信息 */
    private MemberBaseInfoVo memberBaseInfoVo;

    /** 关联会员卡账户 */
    private List<MemberBaseInfoVo> memberBaseInfoVoList;
}