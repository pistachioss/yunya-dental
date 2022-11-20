package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2022/11/7 12:10
 * @description: 患者推荐人数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者推荐人数据模型")
public class PatientSimpleRefererVO implements Serializable {

    /** 姓名 */
    @ApiModelProperty("姓名")
    private String name;

    /** 性别 0-男；1-女；2-未知 */
    @ApiModelProperty("性别 0-男；1-女；2-未知")
    private Byte gender;


    /** 手机号码  */
    @ApiModelProperty("手机号码")
    private String mobile;
}
