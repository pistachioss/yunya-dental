package com.yunya.feign.patient_central.domain.vo.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 简介:
 *
 * @author: ylx
 * @description:
 */
@Data
@ApiModel("企业微信客户绑定下拉患者")
public class WorkWxPatientBindVO {
    @ApiModelProperty("患者姓名")
    private String patientName;
    @ApiModelProperty("患者Id")
    private Integer patientId;
    @ApiModelProperty("关系名称")
    private String dictionaryName;
    @ApiModelProperty("关系字典ID")
    private Integer dictionaryId;
    @ApiModelProperty("患者手机号")
    private String mobile;
    @ApiModelProperty("绑定时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date bindDate;
}
