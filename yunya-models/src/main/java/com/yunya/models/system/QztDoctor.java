package com.yunya.models.system;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "qzt_doctor")
@Data
public class QztDoctor {
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
     * 职称(01-主任医师 02-副主任医师 03-主治医师 04-医师 05-助理医师 10-理疗师)
     */
    @Column(name = "technical_title")
    private String technicalTitle;

    /**
     * 职业范围(多个职业范围逗号隔开  007-口腔 012-医学影像和放射治疗)
     */
    @Column(name = "scope_practice")
    private String scopePractice;

    /**
     * 抗菌药物处方权（0:非抗菌药物 1:非限制级 2:限制级 3:特殊级）
     */
    @Column(name = "antibiosis_authority")
    private String antibiosisAuthority;

    /**
     * 麻醉药品和第一类精神药品处方资格（0:无 1:有）
     */
    @Column(name = "anesthetic_authority")
    private String anestheticAuthority;

    /**
     * 是否是药师（0：否 1：是）
     */
    private String pharmacist;

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