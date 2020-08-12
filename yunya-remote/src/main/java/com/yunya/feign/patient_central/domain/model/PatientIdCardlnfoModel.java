package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 患者wo平台信息
 *
 * @author: WY
 * @date 2020/8/7 19:44
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientIdCardlnfoModel implements Serializable {

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "民族")
    private String nation;

    @ApiModelProperty(value = "出生年月日")
    private String birthday;

    @ApiModelProperty(value = "公民身份号码")
    private String idNum;

    @ApiModelProperty(value = "住址")
    private String address;

    @ApiModelProperty(value = "签发机关")
    private String issuingOrgan;

    @ApiModelProperty(value = "身份证照")
    private String photoPath;

    @ApiModelProperty(value = "有效期限")
    private String usefulLife;

    @ApiModelProperty(value = "比对结果")
    private boolean compareResult;

    @ApiModelProperty(value = "卡编号,部分身份证阅读器不支持读取")
    private String id;

    @ApiModelProperty(value = "比对时间")
    private Integer createTime;

    @ApiModelProperty(value = "比对设备序列号")
    private String deviceKey;

}
