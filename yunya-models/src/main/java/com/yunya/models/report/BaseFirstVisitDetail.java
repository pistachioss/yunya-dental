package com.yunya.models.report;

import lombok.Data;

import javax.persistence.*;

@Table(name = "base_first_visit_detail")
@Data
public class BaseFirstVisitDetail {
    @Id
    private Integer id;

    /**
     * 门诊Id
     */
    @Column(name = "org_id")
    private String orgId;
    /**
     * 门诊名称
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * firstVisitTime
     */
    @Column(name = "first_visit_time")
    private String firstVisitTime;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 患者姓名
     */
    @Column(name = "patient_name")
    private String patientName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 患者来源分类
     */
    @Column(name = "origin_type_name")
    private String originTypeName;

    /**
     * 患者来源
     */
    @Column(name = "origin_name")
    private String originName;

    /**
     * 下次预约时间
     */
    @Column(name = "next_appointment_time")
    private String nextAppointmentTime;

    /**
     * 预约内容
     */
    @Column(name = "next_appoint_content")
    private String nextAppointContent;

    /**
     * 下次提醒时间
     */
    @Column(name = "next_remind_time")
    private String nextRemindTime;

    /**
     * 提醒内容
     */
    @Column(name = "next_remind_content")
    private String nextRemindContent;

    /**
     * 提醒内容
     */
    @Column(name = "registered_dentist_id")
    private String registeredDentistId;

}