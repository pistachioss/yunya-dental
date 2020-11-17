package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 打印信息参数模型
 * @author: LHB
 * @create: 2020-11-17 14:14
 **/
@ApiModel(value = "PrintInfoQuery",description = "打印信息参数模型")
@Data
public class PrintInfoQuery implements Serializable {
    @ApiModelProperty(value = "就诊ID列表",required = true)
    @NotNull(message = "就诊ID列表不能为空")
    private List<Integer> treatmentIds;
}
