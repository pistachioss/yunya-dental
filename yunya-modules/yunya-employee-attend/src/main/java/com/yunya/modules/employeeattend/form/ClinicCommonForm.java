package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-06-12 10:43
 */
@Data
@ApiModel("门诊表单")
public class ClinicCommonForm implements Serializable {
    @ApiModelProperty("基础对象ID")
    @NotNull(message = "基础对象ID不能为空")
    private Integer id;

    @ApiModelProperty("门诊节点")
    @NotNull(message = "门诊节点列表不能为空")
    private List<ClinicNode> clinicNodes;
}
