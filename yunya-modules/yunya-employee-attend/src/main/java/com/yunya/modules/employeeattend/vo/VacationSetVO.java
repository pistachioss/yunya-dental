package com.yunya.modules.employeeattend.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description: 假期设置VO类
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
public class VacationSetVO {

    private Integer id;

    /**
     * 申请类型 0：小时 1：班次 2：天
     */
    @ApiModelProperty("申请类型 0：小时 1：班次 2：天")
    private Integer vacationStatus;

    /**
     * 假期名称
     */
    @ApiModelProperty("假期名称")
    private String vacationName;

    /**
     * 适用范围 0：在职员工 1:正式员工 2：试用期员工
     */
    @ApiModelProperty("适用范围 0：在职员工 1:正式员工 2：试用期员工")
    private Integer vacationRange;

    /**
     * 启用状态 0否 1是
     */
    @ApiModelProperty("启用状态 0否 1是")
    private Integer vacationEnable;

    /**
     * 假期说明
     */
    @ApiModelProperty("假期说明")
    private String vacationExplain;

    /**
     * 创建人
     */
    private Integer crtId;

    /**
     * 创建时间
     */
    private Date crtTime;

    /**
     * 更新人
     */
    private Integer updId;

    /**
     * 更新时间
     */
    private Date updTime;
}
