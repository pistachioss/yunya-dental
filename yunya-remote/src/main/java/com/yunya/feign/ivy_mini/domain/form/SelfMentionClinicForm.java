package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/16
 * @description:
 */
@Data
public class SelfMentionClinicForm {

    private Integer id;
    @ApiModelProperty(value = "门诊Id")
    private Integer clinicId;
}
