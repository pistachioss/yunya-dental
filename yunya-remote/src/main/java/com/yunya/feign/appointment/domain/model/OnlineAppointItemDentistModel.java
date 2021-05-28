package com.yunya.feign.appointment.domain.model;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 线上预约项目医生配置
 * @author: LHB
 * @create: 2021-05-27 12:52
 **/
@Data
@ApiModel(value = "OnlineAppointItemDentistModel",description = "线上预约项目医生配置")
public class OnlineAppointItemDentistModel implements Serializable {
    private Integer id;
    private Integer patientId;
    private Integer orgId;
    private Boolean inservice;
}
