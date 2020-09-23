package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介:
 *
 * @author: YK
 * @date: 2020/9/22 09:32
 * @description: 添加人脸识别人员信息模板
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "添加人脸识别人员信息模板")
public class PersonModel implements Serializable {

    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idcardNum;

    /** id */
    private String id;
}