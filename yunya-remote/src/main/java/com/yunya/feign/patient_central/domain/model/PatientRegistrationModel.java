package com.yunya.feign.patient_central.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /** 患者姓名 字符串，长度64 */
    @ApiModelProperty(value = "患者姓名",required = true)
    @NotEmpty(message = "姓名为空")
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
    @ApiModelProperty("国籍编码")
    private String state;

    /** 通讯地址（行政区划代码）*/
    @ApiModelProperty("通讯地址（行政区划代码）")
    private Integer address;

    /** 详细地址*/
    @ApiModelProperty("详细地址")
    private String detailedAddress;

    /** 电子邮件*/
    @ApiModelProperty("电子邮件")
    private String eMail;

    /** 紧急联系人*/
    @ApiModelProperty("紧急联系人")
    private String emergencyPhone;

    /** 患者来源类型 患者来源分类ID */
    @ApiModelProperty(value = "患者来源分类ID", required = true)
    @NotNull(message = "渠道来源不能为空")
    private Integer originType;

    /** 患者来源关联ID 患者来源关联ID（员工ID/患者ID/活动ID）*/
    @ApiModelProperty(value = "患者来源关联ID",required = true)
    @NotNull(message = "渠道来源不能为空")
    private Integer originId;

    /** 疾病史 */
    @ApiModelProperty("疾病史")
    private List<Integer> medicalHistoryIds;

    /** 过敏源 */
    @ApiModelProperty("过敏源")
    private List<Integer> allergnIds;

    /** 患者签名图片*/
    @ApiModelProperty("患者签名图片")
    private String signatureImgUrl;
}
