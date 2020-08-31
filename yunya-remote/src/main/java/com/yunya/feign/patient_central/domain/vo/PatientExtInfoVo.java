package com.yunya.feign.patient_central.domain.vo;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 患者其他资料Vo
 *
 * @author: WY
 * @date 2020/8/31 11:34
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientExtInfoVo implements Serializable {

    /**
     * 主键
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
     * 字典明细ID
     */
    private Integer dictItemId;

    /**
     * 数据类型 0-标签；1-疾病史；2-过敏原
     */
    private Byte type;

    /**
     * 描述 描述信息
     */
    private String description;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    private Integer crtId;

    /**
     * 创建人姓名
     */
    private String crtName;

    /**
     * 创建时间
     */
    private Date crtTime;

    /**
     * 更新人ID
     */
    private Integer uptId;

    /**
     * 更新人姓名
     */
    private String updName;

    /**
     * 更新时间
     */
    private Date updTime;
}
