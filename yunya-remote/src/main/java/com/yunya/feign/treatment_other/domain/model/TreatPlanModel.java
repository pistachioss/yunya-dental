package com.yunya.feign.treatment_other.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author chenl
 * @description: 治疗剂量参数模型
 * @date: 2021-10-19
 */
@ApiModel(description = "治疗剂量参数模型")
@Data
@ToString
public class TreatPlanModel implements Serializable {
    /** 患者ID*/
    @ApiModelProperty(value = "患者ID", required = true)
    @NotNull(message = "患者ID不能为空")
    private Integer patientId;

    /** 上传文件列表*/
    @ApiModelProperty(value = "上传文件列表", required = true)
    @NotNull(message = "上传文件列表不能为空")
    private List<XUploadFileModel> list;
}