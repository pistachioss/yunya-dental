package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：患者注册信息VO
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/5/23 13:45
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者注册信息VO")
public class PatientRegistrationVO implements Serializable {
    /**  成人注册信息*/
    @ApiModelProperty("成人注册信息")
    private AdultPatientRegistrationVO adultPatient;

    /** 儿童注册信息*/
    @ApiModelProperty("儿童注册信息")
    private ChildrenPatientRegistrationVO childrenPatient;
}
