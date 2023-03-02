package com.yunya.feign.system.vo;

import lombok.Data;

@Data
public class BjSyncDoctorVO {
    private Integer entid;
    /**
     * 医疗机构ID （全诊医学提供）
     */
    private String institutionId;
    /**
     * 医生姓名
     */
    private String doctorId;
    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 身份证
     */
    private String idCard;


    /**
     * 科室名称
     */
    private String departmentName;



    /**
     * 医生资格证号
     */
    private String qualification;

    /**
     * 医生执业证号
     */
    private String doctorLicence;

    /**
     * 职称(01-主任医师 02-副主任医师 03-主治医师 04-医师 05-助理医师 10-理疗师)
     */
    private String technicalTitle;

    /**
     * 职业范围(多个职业范围逗号隔开  007-口腔 012-医学影像和放射治疗)
     */
    private String scopePractice;

    /**
     * 抗菌药物处方权（0:非抗菌药物 1:非限制级 2:限制级 3:特殊级）
     */
    private String antibiosisAuthority;

    /**
     * 麻醉药品和第一类精神药品处方资格（0:无 1:有）
     */
    private String anestheticAuthority;

}