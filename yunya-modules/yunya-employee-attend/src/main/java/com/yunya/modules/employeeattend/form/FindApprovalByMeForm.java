package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class FindApprovalByMeForm {
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "审批状态 0：未审批  1：已审批")
    private Integer appStatus;
}
