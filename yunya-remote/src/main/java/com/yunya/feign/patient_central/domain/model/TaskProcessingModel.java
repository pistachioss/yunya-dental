package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 任务处理结果回调Model
 *
 * @author: YK
 * @date: 2020/9/22 17:58
 * @description: 任务处理结果回调Model
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "任务处理结果回调Model")
public class TaskProcessingModel implements Serializable {

    /** 设备唯一标识码 */
    private String deviceKey;

    /** 处理结果 */
    private String result;

    /** 任务编号 */
    private String taskNo;

    /** 任务编号 */
    private String interfaceName;

}