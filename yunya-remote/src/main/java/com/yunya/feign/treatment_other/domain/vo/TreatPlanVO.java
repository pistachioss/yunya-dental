package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 治疗计划模型
 *
 */
@ApiModel("治疗计划模型")
@Data
@ToString
public class TreatPlanVO implements Serializable {
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("患者ID")
    private Integer patientId;

    @ApiModelProperty("文件ID")
    private Integer fileId;

    /** 文件类型：1-pdf; 2-doc; 3-jgp; 4-png*/
    @ApiModelProperty(value = "文件类型：1-pdf; 2-doc; 3-jgp; 4-png",
            allowableValues = "1-pdf; 2-doc; 3-jgp; 4-png")
    private Byte fileType;

    /** 文件资源定位路径*/
    @ApiModelProperty("文件资源定位路径")
    private String fileLocation;

    /** 文件名*/
    @ApiModelProperty("文件名")
    private String fileName;
}