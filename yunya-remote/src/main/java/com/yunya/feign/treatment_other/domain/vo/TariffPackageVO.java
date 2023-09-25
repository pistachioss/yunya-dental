package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/4 13:19
 * @description: 价目组合数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("价目组合数据模型")
public class TariffPackageVO implements Serializable {

    /** id */
    @ApiModelProperty("id")
    private Integer id;

    /** 名称 */
    @ApiModelProperty("名称")
    private String name;
}
