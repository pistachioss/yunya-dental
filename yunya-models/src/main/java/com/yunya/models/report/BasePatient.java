package com.yunya.models.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "base_patient")
public class BasePatient {
    /**
     * 患者ID
     */
    @Id
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 诊所ID 添加患者的组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 病例编号
     */
    @Column(name = "medical_number")
    private String medicalNumber;

    /**
     * 头像地址
     */
    @Column(name = "face_url")
    private String faceUrl;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 患者来源类型
     */
    @Column(name = "origin_type")
    private Integer originType;

    /**
     * 来源ID
     */
    @Column(name = "origin_id")
    private Integer originId;

    /**
     * 患者性别 0-男；1-女；2-未知
     */
    private Byte gender;

    /**
     * 初诊日期
     */
    @Column(name = "first_visit_date")
    private Date firstVisitDate;

    /**
     * 初诊门诊
     */
    @Column(name = "first_visit_outpatient")
    private String firstVisitOutpatient;

    /**
     * 初诊医生
     */
    @Column(name = "first_visit_doctors")
    private String firstVisitDoctors;

    /**
     * 末诊日期
     */
    @Column(name = "last_visit_date")
    private Date lastVisitDate;

    /**
     * 末诊门诊
     */
    @Column(name = "last_visit_outpatient")
    private String lastVisitOutpatient;

    /**
     * 末诊医生
     */
    @Column(name = "last_visit_doctors")
    private String lastVisitDoctors;

    /**
     * 累计消费
     */
    @Column(name = "cumulative_consumption")
    private Integer cumulativeConsumption;

    /**
     * 欠费总额
     */
    @Column(name = "total_arrears")
    private Integer totalArrears;

    /**
     * 就诊次数
     */
    @Column(name = "number_of_visits")
    private Integer numberOfVisits;

    /**
     * 患者名称拼音
     */
    @Column(name = "pinyin_name")
    private String pinyinName;

    /**
     * 末诊日期
     */
    @Column(name = "patient_crt_time")
    private Date patientCrtTime;

    /** 患者来源名称 */
    @Column(name = "origin_type_name")
    private String originTypeName;



}