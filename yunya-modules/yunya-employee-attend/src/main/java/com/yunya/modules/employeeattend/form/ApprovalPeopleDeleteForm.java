package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

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
public class ApprovalPeopleDeleteForm {

    @ApiModelProperty("审批人设置表id")
    private Integer id;

    @ApiModelProperty("审批人级别设置表id")
    @NotNull(message = "审批人级别设置表id不能为空")
    private Integer approvalLevelId;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @NotNull(message = "用户id不能为空")
    private Integer userId;
}
