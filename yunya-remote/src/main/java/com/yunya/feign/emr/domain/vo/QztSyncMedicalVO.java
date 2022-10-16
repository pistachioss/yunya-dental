package com.yunya.feign.emr.domain.vo;

import lombok.Data;

@Data
public class QztSyncMedicalVO {
    private String entid;
    /**
     * 医疗机构ID （全诊医学提供）
     */
    private String institutionId;
    /**
     * 医疗机构姓名
     */
    private String institutionName;
    /**
     * 科室名称
     */
    private String departmentName;
    /**
     * 医生姓名
     */
    private String doctorName;
    /**
     * 医生Id(对接方医师主键)
     */
    private String doctorId;
    /**
     * 患者id
     */
    private String uid;

    /**
     * 患者姓名
     */
    private String name;
    /**
     * 性别
     * 1		男
     * 2		女
     */
    private Integer gender;
    /**
     * 年龄
     */
    private Integer age;

    /**
     * 就诊日期 yyyy-MM-dd
     */
    private String visitDate;

    /**
     * 主诉
     */
    private String complain;

    /**
     * 诊断 多个以逗号分隔 Z01.251	牙科检查
     */
    private String diagnosis;

    /**
     * 诊断icd10 多个以逗号分隔（二级目录：icd10代码值.xlsx） Z01.251	牙科检查
     */
    private String diagnosisIcd10;
    /**
     * 更新时间 yyyy-MM-dd HH:mm:ss
     */
    private String updateTm;
    /**
     * 创建时间 yyyy-MM-dd HH:mm:ss
     */
    private String createTm;
    /**
     * 处理（诊疗方案）
     */
    private String treatment;
}
