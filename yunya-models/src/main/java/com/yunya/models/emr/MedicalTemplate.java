package com.yunya.models.emr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * @author 
 * 病历模板
 */
@Table(name="medical_template")
@Data
public class MedicalTemplate implements Serializable {
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 病历模板分类Id
     */
    @Column(name = "medical_template_category_id")
    private Integer medicalTemplateCategoryId;

    private String name;

    /**
     * 模板类型 0：初诊  1：复诊
     */
    private Integer type;

    /**
     * 复诊
     */
    @Column(name = "re_examination")
    private String reExamination;

    /**
     * 主诉
     */
    @Column(name = "chief_complaint")
    private String chiefComplaint;

    /**
     * 现病史
     */
    @Column(name = "present_illness")
    private String presentIllness;

    /**
     * 既往史
     */
    @Column(name = "past_history")
    private String pastHistory;

    /**
     * 检查
     */
    private String examination;

    /**
     * 诊断
     */
    private String diagnosis;

    /**
     * 计划
     */
    private String plan;

    /**
     * 处理
     */
    private String treatment;

    /**
     * 处方
     */
    private String prescription;

    /**
     * 启用/禁用
     */
    private Integer enable;

    /**
     * 创建人Id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private LocalDateTime crtTime;

    /**
     * 更新Id
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private LocalDateTime updTime;
}