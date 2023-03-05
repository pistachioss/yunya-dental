package com.yunya.feign.emr.domain.vo;

import lombok.Data;

@Data
public class BjSyncItemDetailVO {
    /**
     * 处方里的药品诊疗明细 id，用于更新开药明细
     */
    private String prescriptionItemId;
    /**
     * 组号
     */
    private String groupNo;
    /**
     * 药品或诊疗名称
     */
    private String drugName;
    /**
     * 药品或诊疗通用名称，没 有的场合可以跟 drugName 相同
     */
    private String drugGenericName;
    /**
     * 药品或诊疗数量，中药的
     */
    private String drugConsumption;
}
