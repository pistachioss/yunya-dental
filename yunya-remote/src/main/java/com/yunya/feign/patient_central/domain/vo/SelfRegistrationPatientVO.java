package com.yunya.feign.patient_central.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：自助登记患者VO
 *
 * @author: chenlin
 * @Description: 自助登记患者VO
 * @Date: 2022/3/2 15:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("自助登记患者VO")
public class SelfRegistrationPatientVO implements Serializable {

    /** 患者id */
    @ApiModelProperty("患者id")
    private Integer patientId;

    /** 患者姓名 */
    @ApiModelProperty("患者姓名")
    private String patientName;

    /** 手机号码 */
    @ApiModelProperty("手机号码")
    private String mobile;

    /** 性别：0-男，1-女 */
    @ApiModelProperty(" 性别：0-男，1-女")
    private Byte gender;

    /** 出生日期 */
    @ApiModelProperty("出生日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    /** 年龄 */
    @ApiModelProperty("年龄")
    private Integer age;

    /** 了解渠道 */
    @ApiModelProperty("了解渠道")
    private String originChannel;

    /** 登记时间 */
    @ApiModelProperty("登记时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date registrationTime;

    /** 患者来源类型 */
    private Integer originType;

    /** 患者来源id */
    private Integer originId;
}
