package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-06-12 11:03
 */
@Data
@ApiModel("门诊排班节点")
public class ClinicNode implements Serializable {
    @NotNull(message = "门诊ID不能为空")
    @ApiModelProperty("门诊ID")
    private Integer clinicId;

    /**
     * 是否启用
     */
    @ApiModelProperty(value = "是否启用")
    private Boolean inservice;
}
