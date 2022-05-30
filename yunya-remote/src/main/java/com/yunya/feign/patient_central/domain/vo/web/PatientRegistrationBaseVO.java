package com.yunya.feign.patient_central.domain.vo.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介：患者自助登记基础信息
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/5/27 11:22
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者自助登记基础信息")
public class PatientRegistrationBaseVO implements Serializable {

    /** 患者id*/
    @ApiModelProperty("患者id")
    private Integer id;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id")
    private Integer orgId;

    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String name;

    /** 手机号码 */
    @ApiModelProperty(value = "手机号")
    private String mobile;

    /** 性别 0-男；1-女； */
    @ApiModelProperty(value = "性别 0-男；1-女")
    private Byte gender;

    /** 出生日期 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "出生日期")
    private Date birthdate;

    /** 年龄 */
    @ApiModelProperty(value = "年龄")
    private Integer age;

    /** 国籍 */
    @ApiModelProperty("国籍字典id")
    private Integer state;

    /** 职业-字典id */
    @ApiModelProperty("职业-字典id")
    private Integer profession;

    /** 通讯地址-省份*/
    @ApiModelProperty("通讯地址-省份")
    private String province;

    /** 通讯地址-城市*/
    @ApiModelProperty("通讯地址-城市")
    private String city;

    /** 通讯地址-区县*/
    @ApiModelProperty("通讯地址-区县")
    private String country;

    /** 详细地址*/
    @ApiModelProperty("详细地址")
    private String detailedAddress;

    /** e-mail*/
    @ApiModelProperty("e-mail")
    private String eMail;

    /** 紧急联系人*/
    @ApiModelProperty("紧急联系人")
    private String emergencyPhone;

    /** 疾病史 */
    @ApiModelProperty("疾病史 (疾病名称列表)")
    private List<String> medicalHistorys;

    /** 过敏源 */
    @ApiModelProperty("过敏源 （过敏源名称列表）")
    private List<String> allergns;

    /** 患者签名图片*/
    @ApiModelProperty("患者签名图片")
    private String signatureImgUrl;

    /** 患者签名图片完整路径*/
    @ApiModelProperty("患者签名图片完整路径")
    private String signatureImgPath;
}
