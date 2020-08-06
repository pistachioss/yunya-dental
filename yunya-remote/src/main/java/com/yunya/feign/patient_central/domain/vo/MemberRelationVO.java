package com.yunya.feign.patient_central.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/7/30 15:48
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class MemberRelationVO implements Serializable {

    private  List<PatientMemberRelationVO> MemberRelationList;

    private  List<PatientMemberRelationVO> MemberBalanceRelationList;
}
