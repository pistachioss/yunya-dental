package com.yunya.feign.patient_central.domain.model;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/7/30 17:49
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class MemberBindingRelationInfoModel implements Serializable {

    /**   * 患者id
     */
    private Integer patientId;

    /**
     * 诊所Id
     */
    private Integer orgId;

    /**
     * 关联人名称
     */
    private String name;

    /**
     * 主卡会员人ID
     */
    private Integer masterCardId;

    /**
     * 副卡会员人ID
     */
    private Integer secondaryCardId;

    /**
     * 关联类型
     */
    private Integer bindType;

}
