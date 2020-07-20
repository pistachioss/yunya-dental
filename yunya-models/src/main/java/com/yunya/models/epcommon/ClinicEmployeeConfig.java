package com.yunya.models.epcommon;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author 
 * 门诊员工配置
 */
@Data
public class ClinicEmployeeConfig implements Serializable {
    private Integer id;

    /**
     * 员工ID
     */
    private Integer employeeId;

    /**
     * 门诊ID
     */
    private Integer clinicId;

    /**
     * 默认助手ID
     */
    private Integer assistantEmployeeId;

    /**
     * 门诊默认科室ID
     */
    private Integer clinicDepartmentRoomId;

    /**
     * 是否可预约
     */
    private Integer enableAppoint;

    /**
     * 是否可挂号
     */
    private Integer enableRegistry;

    /**
     * 是否可下载影像
     */
    private Integer enableDownload;

    /**
     * 是否启用
     */
    private Integer inservice;

    /**
     * 创建人ID
     */
    private Integer crtId;

    /**
     * 创建人名称
     */
    private String crtName;

    /**
     * 创建时间
     */
    private LocalDateTime crtTime;

    private Integer updId;

    /**
     * 修改人名称
     */
    private String updName;

    /**
     * 修改时间
     */
    private LocalDateTime updTime;
}