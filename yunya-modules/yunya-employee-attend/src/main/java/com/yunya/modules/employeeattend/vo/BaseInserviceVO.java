package com.yunya.modules.employeeattend.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-06-12 13:22
 */
@Data
public class BaseInserviceVO implements Serializable {
    private Integer clinicId;
    private String clinicName;
    private Boolean inservice;
}
