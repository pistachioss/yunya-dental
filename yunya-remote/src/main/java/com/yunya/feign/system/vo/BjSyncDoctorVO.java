package com.yunya.feign.system.vo;

import lombok.Data;

@Data
public class BjSyncDoctorVO {
    /**
     * 监管机构注册码（向接口 提供方申请）
     */
    private String organizationCode;
    /**
     * 医生姓名
     */
    private String doctorId;
    /**
     * 医生姓名
     */
    private String doctorName;

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

}