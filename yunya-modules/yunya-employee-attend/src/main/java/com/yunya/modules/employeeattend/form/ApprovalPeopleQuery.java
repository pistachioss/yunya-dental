package com.yunya.modules.employeeattend.form;

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
public class ApprovalPeopleQuery {
    @ApiModelProperty("审批人级别设置表id")
    @NotNull(message = "审批人级别设置表id不能为空")
    private Integer approvalLevelId;

    @ApiModelProperty("访问来源是否为PC端")
    private Integer isPc = 0;

    @ApiModelProperty("页数")
    private Integer page = 1;

    @ApiModelProperty("每页个数")
    private Integer size = 10;

    @ApiModelProperty("是否分页")
    private Boolean whetherPage = false;
}
