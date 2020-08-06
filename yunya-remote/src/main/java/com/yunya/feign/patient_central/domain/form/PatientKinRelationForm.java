package com.yunya.feign.patient_central.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/3 14:59
 * @description: 患者亲属关系修改模板
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者亲属关系添加模板")
public class PatientKinRelationForm implements Serializable {
    /**
     *  主键ID
     */
    @ApiModelProperty(value = "主键ID",required = true)
    @NotNull(message = "主键ID不能为空！")
    private Integer id;
    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;

    /**
     * 患者ID
     */
    @NotNull(message = "患者id不能为空！")
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    /**
     * 关联患者ID
     */
    @NotNull(message = "关联患者ID不能为空！")
    @ApiModelProperty(value = "关联患者ID",required = true)
    private Integer linkedPatientId;

    /**
     * 亲属关系字典类型ID 字典管理
     */
    @NotNull(message = "亲属关系字典类型ID不能为空！")
    @ApiModelProperty(value = "亲属关系字典类型ID",required = true)
    private Integer kinshipId;

    /**
     * 备注 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;
}
