package com.yunya.feign.emr.domain.vo;

import lombok.Data;

@Data
public class BjSyncMedicalVO {
    private String medicalRecordId;
    /**
     * 患者姓名
     */
    private String patientName;
    /**
     * 性别代码：1：男性 2：女性 9： 未声明的性别 0：未知的性别
     */
    private Integer patientSex;
    /**
     * 监管机构注册码（向接口提供方 申请）
     */
    private String organizationCode;
    /**
     * 科室名称
     */
    private String departmentName;
    /**
     * 医生Id(对接方医师主键)
     */
    private String doctorId;
    /**
     * 医生姓名
     */
    private String doctorName;
    /**
     * 就诊时间，格式 yyyy-MM-dd HH:mm:ss
     */
    private String medicalTime;
    /**
     * 门急诊类型 0:普通门诊;1:专家门 诊;2:急诊;3:住院
     */
    private Integer medicalType;
    private Integer firstVisit;
    /**
     * 疾病名称，多个疾病使用【,】分 隔
     */
    private String diagnose;
    /**
     * 诊断icd10 多个以逗号分隔（二级目录：icd10代码值.xlsx） Z01.251	牙科检查
     */
    private String icd10;
    /**
     * 病情描述，没有的场合可以为空
     */
    private String diseaseDesc;
    /**
     * 既往史，没有的场合可以为空
     */
    private String pastHistory;
    /**
     * 过敏史，没有的场合可以为空
     */
    private String allergyHistory;
    /**
     * 治疗意见，没有的场合可以为空
     */
    private String operation;
}
