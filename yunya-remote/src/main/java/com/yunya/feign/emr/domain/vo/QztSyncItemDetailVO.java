package com.yunya.feign.emr.domain.vo;

import lombok.Data;

@Data
public class QztSyncItemDetailVO {
    private String itemName;
    /**
     * 门诊病历ID
     */
    private String specification;
    /**
     * 患者姓名
     */
    private String manufacturer;

    public QztSyncItemDetailVO(String itemName) {
        this.itemName = itemName;
    }
}
