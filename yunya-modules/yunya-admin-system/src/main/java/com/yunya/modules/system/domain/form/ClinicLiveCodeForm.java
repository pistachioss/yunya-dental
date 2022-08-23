package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：门店店长活码编辑模型
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/5/18 17:05
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门店店长活码编辑模型")
public class ClinicLiveCodeForm implements Serializable {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id", required = true)
    @NotNull(message = "主键id不能为空")
    private Integer id;

    /**
     * 门诊id
     */
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊id不能为空")
    private Integer orgId;
}
