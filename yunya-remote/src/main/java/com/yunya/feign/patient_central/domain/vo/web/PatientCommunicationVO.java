package com.yunya.feign.patient_central.domain.vo.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: chenlin
 * @date: 2022/11/1 9:40
 * @description: 患者沟通记录数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者沟通记录数据模型")
public class PatientCommunicationVO implements Serializable {

    /** 沟通记录id */
    @ApiModelProperty("沟通记录id")
    private Integer id;

    /** 沟通内容 */
    @ApiModelProperty("沟通内容")
    private String content;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @ApiModelProperty("创建时间")
    private Date crtTime;

    /** 创建人 */
    @ApiModelProperty("创建人")
    private String crtName;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @ApiModelProperty("修改时间")
    private Date updTime;

    /** 修改人 */
    @ApiModelProperty("修改人")
    private String updName;
}
