package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：症状设置VO
 *
 * @author: chenlin
 * @Description: 症状设置VO
 * @Date: 2022/1/9 13:20
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("症状设置VO")
public class SymptomConfigVO implements Serializable {
    /** 症状id*/
    @ApiModelProperty("症状id")
    private Integer id;

    /** 症状名称*/
    @ApiModelProperty("症状名称")
    private String symptomName;

    /** 备注*/
    @ApiModelProperty("备注")
    private String remark;
}
