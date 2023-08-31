package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/8/31 15:19
 * @description: 全程医疗返回对象
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗返回对象")
public class QcMedicalResult implements Serializable {

    /** 成功标识 */
    @ApiModelProperty("成功标识")
    private String code;

    /** 提示信息 */
    @ApiModelProperty("提示信息")
    private String msg;
}
