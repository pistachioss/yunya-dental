package com.yunya.models.appointment;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 预约操作记录表
 * appointment_operate_record
 * @author 
 */
@Data
@ToString
public class AppointmentOperateRecord implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 诊所ID
     */
    private Integer orgId;

    /**
     * 预约ID
     */
    private Integer appointmentId;

    /**
     * 操作类型 操作记录(0-新建预约；1-修改预约；2-取消预约；3-确认预约；4；取消确认)
     */
    private Byte operateType;

    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 创建人ID
     */
    private Integer crtId;

    /**
     * 创建人姓名
     */
    private String crtName;

    /**
     * 创建时间
     */
    private Date crtTime;

    /**
     * 更新人ID
     */
    private Integer uptId;

    /**
     * 更新人姓名
     */
    private String updName;

    /**
     * 更新时间
     */
    private Date updTime;

    private static final long serialVersionUID = 1L;
}