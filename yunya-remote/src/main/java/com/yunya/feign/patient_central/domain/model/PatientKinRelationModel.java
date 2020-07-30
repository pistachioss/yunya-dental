package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.security.PrivateKey;

/**
 * 简单介绍:</br> 患者亲属关系Vo
 *
 * @author: WY
 * @date 2020/7/29 10:13
 * @description: 患者亲属关系添加模板
 * @since: 1.0.0
 */

@Data
@ToString
@ApiModel("患者亲属关系添加模板")
public class PatientKinRelationModel implements Serializable {

    /**
     *  主键ID
     */
    private Integer id;
    /**
     * 诊所ID
     */
    private Integer orgId;

    /**
     * 患者ID
     */
    private Integer patientId;

    /**
     * 关联患者ID
     */
    private Integer linkedPatientId;

    /**
     * 亲属关系字典类型ID 字典管理
     */
    private Integer kinshipId;

    /**
     * 备注 备注
     */
    private String remarks;


}
