package com.yunya.modules.employeeattend.vo;

import lombok.Data;

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
public class EmListVO {
    /**
     * 用户id
     */
    private Integer employeeId;
    /**
     * 门诊id
     */
    private Integer clinicId;
    /**
     * 排班表ID
     */
    private Integer scheduleId;
    /**
     * 工作日
     */
    private Date workDate;
    /**
     * 班次属性
     */
    private String type;
    /**
     * 班次开始时间
     */
    private Date startTime;
    /**
     * 班次结束时间
     */
    private Date endTime;
}
