package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br>患者扩展信息
 *
 * @author: WY
 * @date 2020/8/26 13:01
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientExtInfoModel implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Integer id;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID", required = true)
    @NotNull(message = "诊所id不能为空")
    private Integer orgId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID", required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;

    /**
     * 字典明细ID
     */
    @ApiModelProperty(value = "字典明细ID")
    private Integer dictItemId;

    /**
     * 数据类型 0-标签；1-疾病史；2-过敏原；3-患者诊疗需求
     */
    @ApiModelProperty(value = "数据类型 0-标签；1-疾病史；2-过敏原；3-患者诊疗需求", required = true)
    @NotNull(message = "数据类型不能为空")
    private Byte type;

    /**
     * 描述 描述信息
     */
    @ApiModelProperty(value = " 描述 描述信息")
    private String description;

    /**
     * 备注 备注
     */
    @ApiModelProperty(value = "备注 备注")
    private String remarks;

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

    /**
     * 创建人ID
     */
    private Integer crtId;

}
