package com.yunya.feign.patient_central.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>编辑患者头像
 *
 * @author: WY
 * @date 2020/8/21 11:29
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientPhotoForm implements Serializable {

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id",required = true)
    private Integer id;

    /**
     * 患者头像
     */
    @ApiModelProperty(value = "患者头像",required = true)
    private String faceUrl;
}
