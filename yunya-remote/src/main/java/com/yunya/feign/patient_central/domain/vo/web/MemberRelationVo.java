package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简单介绍:</br> 返回会员绑定关系模型
 *
 * @author: WY
 * @date 2020/7/30 15:48
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回会员绑定关系模型")
public class MemberRelationVo implements Serializable {

    private  List<PatientMemberRelationVo> MemberRelationList;

    private  List<PatientMemberRelationVo> MemberBalanceRelationList;
}
