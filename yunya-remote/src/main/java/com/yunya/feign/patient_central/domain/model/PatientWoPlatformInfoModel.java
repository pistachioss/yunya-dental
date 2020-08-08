package com.yunya.feign.patient_central.domain.model;

import com.yunya.feign.patient_central.domain.vo.PatientIdCardlnfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>患者WO平台信息
 *
 * @author: WY
 * @date 2020/8/7 19:33
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者WO平台信息模板")
public class PatientWoPlatformInfoModel implements Serializable {

    @ApiModelProperty(value = "识别guid,唯一不重复")
    private String guid;

    @ApiModelProperty(value = "设备序列号")
    private String deviceKey;

    @ApiModelProperty(value = "人员guid，陌生人为STRANGERBABY，人证比对为IDCARDBABY")
    private String personGuid;

    @ApiModelProperty(value = "现场照url")
    private String photoUrl;

    @ApiModelProperty(value = "识别记录时间,0时区时间,如(2018-11-28T03:06:23+0000)")
    private String showTime;

    @ApiModelProperty(value = "人员比对结果,1:比对成功 2:比对失败")
    private Integer type;

    @ApiModelProperty(value = "识别模式,1:刷脸,2:刷卡,3:脸&卡双重认证, 4:人证比对")
    private Integer recMode;

    @ApiModelProperty(value = "识别卡号")
    private String cardNo;

    @ApiModelProperty(value = "活体结果 1:活体判断成功 2:活体判断失败 3:未进行活体判断")
    private Integer aliveType;

    @ApiModelProperty(value = "比对模式,1:本地识别 2:云端识别")
    private Integer recType;

    @ApiModelProperty(value = "人员姓名")
    private String personName;

    @ApiModelProperty(value = "有效时间段判断 1:时间段内 2:时间段外 3:未进行时间段判断")
    private Integer passTimeType;

    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    @ApiModelProperty(value = "有效日期判断 1:有效期内 2:有效期外 3:未进行有效期判断")
    private Integer permissionTimeType;

    @ApiModelProperty(value = "识别模式判断 1. 模式正确 2.模式不正确")
    private String recModeType;

    @ApiModelProperty(value = "患者人员信息")
    private PatientIdCardlnfoVo idCardInfo;


}
