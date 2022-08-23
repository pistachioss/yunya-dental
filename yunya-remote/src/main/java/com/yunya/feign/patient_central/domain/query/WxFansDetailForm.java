package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
@ApiModel(value = "WxFansDetailForm",description = "客服中心查看详情参数模型")
public class WxFansDetailForm {

    @ApiModelProperty("unionId")
    @NotNull(message = "unionId不能为空")
    private String unionId;
}
