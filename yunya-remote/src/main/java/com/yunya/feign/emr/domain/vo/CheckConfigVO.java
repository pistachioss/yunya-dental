package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：检查设置VO
 *
 * @author: chenlin
 * @Description: 检查设置VO
 * @Date: 2022/1/9 9:49
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("检查设置VO")
public class CheckConfigVO implements Serializable {
    /** 检查id*/
    @ApiModelProperty("检查id")
    private Integer id;

    /** 检查名称*/
    @ApiModelProperty("检查名称")
    private String checkName;
}
