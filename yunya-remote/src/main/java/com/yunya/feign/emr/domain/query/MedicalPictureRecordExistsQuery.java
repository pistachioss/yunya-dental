package com.yunya.feign.emr.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：病历照片记录是否存在查询模型
 *
 * @author: chenlin
 * @Description: 病历照片记录是否存在查询模型
 * @Date: 2022/3/24 10:06
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("病历照片记录是否存在查询模型")
public class MedicalPictureRecordExistsQuery implements Serializable {
    /** 记录i*/
    @ApiModelProperty("记录id")
    private Integer id;

    /** 患者id*/
    @ApiModelProperty(value = "患者id",required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;

    /** 日期*/
    @ApiModelProperty(value = "日期", required = true)
    @NotEmpty(message = "日期不能为空")
    private String name;
}
