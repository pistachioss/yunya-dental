package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：门店店长活码添加模型
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/5/18 17:05
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门店店长活码添加模型")
public class ClinicLiveCodeModel implements Serializable {

    /**
     * 门诊id
     */
    @ApiModelProperty("门诊id")
    private Integer orgId;
}
