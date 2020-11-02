package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2020/11/2 13:52
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回标签记录信息参数模型")
public class PatientLabelRecordVo implements Serializable {

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
     * 备注 备注
     */
    private String remarks;

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
     * 操作类型
     */
    private Integer operatingType;

    /**
     * 创建人姓名
     */
    private String dictItemName;
}