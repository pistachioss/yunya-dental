package com.yunya.models.expand;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 
 * 门诊员工配置
 */
@Table(name = "clinic_employee_config")
@Data
public class ClinicEmployeeConfig implements Serializable {
    @Id
    private Integer id;

    /**
     * 员工ID
     */
    @Column(name = "employee_id")
    private Integer employeeId;

    /**
     * 门诊ID
     */
    @Column(name = "clinic_id")
    private Integer clinicId;

    /**
     * 默认助手ID
     */
    @Column(name = "assistant_employee_id")
    private Integer assistantEmployeeId;

    /**
     * 门诊默认科室ID
     */
    @Column(name = "clinic_department_room_id")
    private Integer clinicDepartmentRoomId;

    /**
     * 是否可预约
     */
    @Column(name = "enable_appoint")
    private Integer enableAppoint;

    /**
     * 是否可挂号
     */
    @Column(name = "enable_registry")
    private Integer enableRegistry;

    /**
     * 是否可下载影像
     */
    @Column(name = "enable_download")
    private Integer enableDownload;

    /**
     * 是否启用
     */
    private Integer inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private LocalDateTime crtTime;

    @Column(name = "upd_id")
    private Integer updId;
    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private LocalDateTime updTime;
}