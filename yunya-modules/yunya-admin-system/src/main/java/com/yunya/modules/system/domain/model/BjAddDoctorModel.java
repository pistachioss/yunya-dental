package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 简介: 岗位新增参数模型
 *
 * @author: chow
 * @date: 2020/7/27 19:51
 * @description:
 * @since: 1.0.0
 */
@Data
@ApiModel("完善滨江麟康医生")
public class BjAddDoctorModel {
    /**
     * 是否开启医生认证（0-否 1-是）
     */
    @ApiModelProperty(value = "是否开启医生认证（0-否 1-是）", required = true)
    private Boolean enableCert;

    /**
     * 医生姓名
     */
    @ApiModelProperty(value = "医生姓名", required = true)
    private String doctorName;

    /**
     * 身份证
     */
    @ApiModelProperty(value = "身份证", required = true)
    @Size(max = 18, message = "身份证号长度不能超过18个字符")
    private String idCard;

    /**
     * 医生资格证号
     */
    @ApiModelProperty(value = "医生资格证号", required = true)
    private String qualification;

    /**
     * 医生执业证号
     */
    @ApiModelProperty(value = "医生执业证号", required = true)
    private String doctorLicence;


    /**
     * 执业门诊(多选逗号)
     */
    @ApiModelProperty(value = "执业门诊(多选逗号)", required = true)
    private String practiceClinic;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private Integer userId;
}
