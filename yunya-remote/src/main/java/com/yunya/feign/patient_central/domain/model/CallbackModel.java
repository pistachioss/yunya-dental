package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介:
 *
 * @author: YK
 * @date: 2020/9/22 09:59
 * @description: 心跳回调模板
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "心跳回调模板")
public class CallbackModel implements Serializable {

    /** 设备唯一标识码 */
    private String deviceKey;

    /** 设备当前时间戳 */
    private String time;

    /** 设备当前 IP 地址 */
    private String ip;

    /** 设备当前注册人员数量 */
    private String personCount;

    /** 设备当前注册的照片数量 */
    private String faceCount;

    /** 设备版本号 */
    private String version;
}