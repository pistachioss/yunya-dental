package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 简介: 假期设置添加form
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
public class VacationSetForm {

    @ApiModelProperty("id")
    private Integer id;

    /**
     * 申请类型 0：小时 1：班次 2：天
     */
    @NotNull(message = "申请类型不能为空")
    @ApiModelProperty("申请类型")
    private Integer vacationStatus;

    /**
     * 假期名称
     */
    @NotNull(message = "假期名称不能为空")
    @ApiModelProperty("假期名称")
    private String vacationName;

    /**
     * 适用范围 0：在职员工 1:正式员工 2：试用期员工
     */
    @NotNull(message = "适用范围不能为空")
    @ApiModelProperty("适用范围")
    private Integer vacationRange;

    /**
     * 启用状态 0否 1是
     */
    @NotNull(message = "启用状态不能为空")
    @ApiModelProperty("启用状态")
    private Integer vacationEnable;

    /**
     * 假期说明
     */
    @ApiModelProperty("假期说明")
    private String vacationExplain;

    /**
     * 创建人
     */
    @ApiModelProperty("crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @ApiModelProperty("crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
    @ApiModelProperty("upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @ApiModelProperty("upd_time")
    private Date updTime;
}
