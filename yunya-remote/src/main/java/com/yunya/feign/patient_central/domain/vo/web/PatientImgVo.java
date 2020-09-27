package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 简介: 患者照片Vo
 *
 * @author: WY
 * @date: 2020/9/24 20:01
 * @description: 患者照片Vo
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "患者照片Vo")
public class PatientImgVo implements Serializable {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * 患者图1（头像）
     */
    private String imgOne;

    /**
     * 患者图2
     */
    private String imgTwo;

    /**
     * 患者图3
     */
    private String imgThree;

    /**
     * 设备照片图1id
     */
    private String faceIdOne;

    /**
     * 设备照片图2id
     */
    private String faceIdTwo;

    /**
     * 设备照片图3id
     */
    private String faceIdThree;
}