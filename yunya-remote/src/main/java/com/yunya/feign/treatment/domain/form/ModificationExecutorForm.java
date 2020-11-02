package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 患者档案-修改执行人
 * @author: LHB
 * @create: 2020-10-31 10:32
 **/
@Data
@ApiModel(value = "ModificationExecutorForm",description = "患者档案-修改执行人")
public class ModificationExecutorForm implements Serializable {
    /** 订单明细ID */
    @ApiModelProperty(name = "id",value = "订单明细ID",required = true)
    @NotNull(message = "订单明细ID不能为空")
    private Integer id;
    /** 修改执行人ID */
    @ApiModelProperty(name = "executorId",value = "修改执行人ID",required = true)
    @NotNull(message = "修改执行人ID不能为空")
    private Integer executorId;
}
