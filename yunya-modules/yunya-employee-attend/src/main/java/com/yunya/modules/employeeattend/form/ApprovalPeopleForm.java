package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
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
public class ApprovalPeopleForm {

    @ApiModelProperty("审批人级别设置表id")
    @NotNull(message = "审批人级别设置表id不能为空")
    private Integer approvalLevelId;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id集合")
    @NotNull(message = "用户id集合不能为空")
    private List<Integer> userId;

}
