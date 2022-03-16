package com.yunya.feign.patient_central.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介：患者登记添加模型
 *
 * @author: chenlin
 * @Description: 患者登记添加模型
 * @Date: 2022/2/28 13:06
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者登记添加模型")
public class PatientRegistrationModel implements Serializable {

    /** 门诊id */
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊id不能为空")
    private Integer orgId;

    /** 患者姓名 字符串，长度64 */
    @ApiModelProperty(value = "患者姓名",required = true)
    @NotEmpty(message = "姓名为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符！")
    private String name;

    /** 性别 0-男；1-女； */
    @ApiModelProperty(value = "性别 0-男；1-女")
    private Byte gender;

    /** 出生日期 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "出生日期",required = true)
    @NotNull(message = "出生日期不能为空")
    private Date birthdate;

    /** 年龄 */
    @ApiModelProperty(value = "年龄", required = true)
    @NotNull(message = "年龄不能为空")
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
    @Size(max = 50, message = "详细长度不能超过50个字符！")
    private String detailedAddress;

    /** e-mail*/
    @ApiModelProperty("e-mail")
    @Size(max = 50, message = "电子邮箱长度不能超过50个字符！")
    private String eMail;

    /** 紧急联系人*/
    @ApiModelProperty("紧急联系人")
    @Size(max = 50, message = "紧急联系人长度不能超过50个字符！")
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
}
