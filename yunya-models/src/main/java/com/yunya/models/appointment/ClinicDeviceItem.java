package com.yunya.models.appointment;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * clinic_device_item
 * @author 
 */
@Data
public class ClinicDeviceItem implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 预约设备类型ID
     */
    private Integer deviceId;

    /**
     * 设备编号
     */
    private String number;

    /**
     * 诊所ID
     */
    private Integer orgId;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;

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