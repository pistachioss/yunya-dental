package com.yunya.models.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "base_patient_origin")
public class BasePatientOrigin {
    /**
     * 患者来源ID
     */
    @Column(name = "id")
    private Integer id;

    /**
     * 患者来源父ID（顶级为0）
     */
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 患者来源名称
     */
    private String name;

    /**
     * 患者来源类型
     */
    @Column(name = "origin_type")
    private Integer originType;

    /**
     * 患者来源属性字典id
     */
    @Column(name = "source_attribute")
    private Integer sourceAttribute;

    /**
     * 二维码地址
     */
    @Column(name = "qr_code_path")
    private String qrCodePath;

    /**
     * 是否允许操作（编辑、删除）
     */
    @Column(name = "allow_operate")
    private Boolean allowOperate;

    /**
     * 是否有时间限制（0-否；1-是））
     */
    @Column(name = "time_limit")
    private Integer timeLimit;

    /**
     * 限制开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "limit_start_date")
    private Date limitStartDate;

    /**
     * 限制时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "limit_end_date")
    private Date limitEndDate;

    /**
     * 是否启用
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 更新人姓名
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */

    @Column(name = "upd_time")
    private Date updTime;

}