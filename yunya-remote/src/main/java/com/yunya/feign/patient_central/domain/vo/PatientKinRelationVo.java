package com.yunya.feign.patient_central.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 患者亲属VO
 *
 * @author: WY
 * @date 2020/7/29 9:32
 * @description: 患者亲属信息列表
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientKinRelationVo implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 患者ID
     */
    private Integer patientId;

    /**
     * 关联患者ID
     */
    private Integer linkedPatientId;

    /**
     * 关系人姓名
     */
    private String relationName;

    /**
     * 亲属关系字典类型ID 字典管理
     */
    private Integer kinshipId;

    /**
     * 性别 0-男；1-女；2-未知
     */
    private Byte gender;

    /**
     * 手机号码 长度14
     */
    private String mobile;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 创建时间
     */
    private Date crtTime;


}
