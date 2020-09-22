package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介:
 *
 * @author: YK
 * @date: 2020/9/22 12:58
 * @description: 回调任务Model
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "回调任务Model")
public class TaskModel implements Serializable {

    /** 设备唯一标识码 */
    private String deviceKey;
}