package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 简介:
 * <$>
 *
 *@author: 杨柳絮
 *@date: $ $
 *@description:
 *@since: 1.0.0
 *@param: $
 *@return: $
 */
@Data
public class ApprovalCriteriaForm {
   @Id
    private Integer id;

    /**
     * 请假跨度起始天数
     */
    @ApiModelProperty("请假跨度起始天数")
    private Integer startDay;

    /**
     * 请假跨度结束天数
     */
    @ApiModelProperty("请假跨度结束天数")
    private Integer endDay;

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
