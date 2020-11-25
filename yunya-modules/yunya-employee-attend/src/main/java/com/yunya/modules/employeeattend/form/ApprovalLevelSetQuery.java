package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

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
public class ApprovalLevelSetQuery {
    @Id
    private Integer id;

    /**
     * 审批条件表id
     */
    @ApiModelProperty("审批条件表id")
    private Integer approvalCriteriaId;

    /**
     * 审批级别名称
     */
    @ApiModelProperty("审批级别名称")
    private String approvalLevelName;

    /**
     * 审批优先级 数字越小级别越高
     */
    @ApiModelProperty("审批优先级 数字越小级别越高")
    private Integer approvalPriority;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private Integer crtId;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date crtTime;

    /**
     * 更新人
     */
    @ApiModelProperty("更新人")
    private Integer updId;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updTime;
}
