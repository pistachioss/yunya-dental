package com.yunya.models.system;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "bj_doctor")
@Data
public class BjDoctor {
    @Id
    private Integer id;

    /**
     * 是否开启医生认证（0-否 1-是）
     */
    @Column(name = "enable_cert")
    private Boolean enableCert;

    /**
     * 医生姓名
     */
    @Column(name = "doctor_name")
    private String doctorName;

    /**
     * 身份证
     */
    @Column(name = "id_card")
    private String idCard;

    /**
     * 科室id
     */
    @Column(name = "depart_id")
    private Integer departId;

    /**
     * 科室名称
     */
    @Column(name = "depart_name")
    private String departName;

    /**
     * 医疗机构ID （全诊医学提供）
     */
    @Column(name = "institution_id")
    private String institutionId;

    /**
     * 医生资格证号
     */
    private String qualification;

    /**
     * 医生执业证号
     */
    @Column(name = "doctor_licence")
    private String doctorLicence;


    /**
     * 执业门诊(多选逗号)
     */
    @Column(name = "practice_clinic")
    private String practiceClinic;

    /**
     * 医生用户关联ids（逗号分隔）
     */
    @Column(name = "relate_user_ids")
    private String relateUserIds;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;
}