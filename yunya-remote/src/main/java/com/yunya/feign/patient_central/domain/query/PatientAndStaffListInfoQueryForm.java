package com.yunya.feign.patient_central.domain.query;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 模糊查询员工和患者信息QueryForm
 *
 * @author: WY
 * @date 2020/8/13 11:29
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientAndStaffListInfoQueryForm implements Serializable {

    /**
     * 患者来源类型
     */
    private Integer originType;

    /**
     * 患者or员工姓名
     */
    private String name;


}
