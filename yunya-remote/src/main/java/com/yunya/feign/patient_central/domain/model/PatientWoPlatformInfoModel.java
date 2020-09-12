package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>患者硬件识别回调信息模板
 *
 * @author: WY
 * @date 2020/8/7 19:33
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者硬件识别回调信息模板")
public class PatientWoPlatformInfoModel implements Serializable {
    
   /* @ApiModelProperty(value = "识别guid,唯一不重复")
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
    private String recModeType;*/


    @ApiModelProperty(value = "设备序列号")
    private String deviceKey;

    @ApiModelProperty(value = "识别记录毫秒级时间戳（以设备 时间为准）")
    private String time;

    @ApiModelProperty(value = "设备当前 IP 地址")
    private String ip;

    @ApiModelProperty(value = "人员 ID 陌生人为 STRANGERBABY 人证比对为 IDCARD")
    private String personId;

    @ApiModelProperty(value = "现场照在设备内的保存路径，访 问此 url 需设备局域网在线，且 发送请求的客户端与设备处于 局域网同一网段")
    private String path;

    @ApiModelProperty(value = "personId 对应的 cardNo")
    private String idcardNum;

    @ApiModelProperty(value = "识别模式 0 刷脸； 1 人脸&卡双重验证； 2 人证比对； 3 刷卡； 4 开门按钮开门； 5 远程开门； 8 口罩检测")
    private String model;

    @ApiModelProperty(value = "活体判断结果 1：活体判断成功 2：活体判断失败 3：未进行活体判断")
    private String aliveType;

    @ApiModelProperty(value = "人员比对结果 1：比对成功 2：比对失败 3：未进行比对")
    private String identifyType;

    @ApiModelProperty(value = "有效时间段判断 1：时间段内 2：时间段外 3：未进行时间段判断")
    private String passTimeType;

    @ApiModelProperty(value = "有效日期判断 1：有效期内 2：有效期外 3：未进行有效期判断")
    private String permissionTimeType;

    @ApiModelProperty(value = "识别模式判断 1：识别模式权限正确 2：识别模式权限不足 3：未进行识别模式判断")
    private String recModeType;

    @ApiModelProperty(value = "身份证信息")
    private String data;

    @ApiModelProperty(value = "识别方式_人员类型 识别方式： face/faceAndcard/idcard/card 人员类型： 0：时间段内 1：时间段外 2：陌生人/识别失败")
    private String type;

    @ApiModelProperty(value = "现场照 base64 码")
    private String base64;

    @ApiModelProperty(value = "识别方式 1:本地识别 2:云端识别")
    private String recType;

    @ApiModelProperty(value = "人员测量温度值(仅口罩测温 设备支持)")
    private String temperature;

    @ApiModelProperty(value = "设置的体温异常标准(仅口罩 测温设备支持)")
    private String standard;

    @ApiModelProperty(value = "体温状态(仅口罩测温设备支 持) 1：正常 2：异常 3：未开启测温 4：测温打开，但口罩检测未通 过")
    private String temperatureState;

    @ApiModelProperty(value = "温度单位(仅口罩测温设备支 持) 1. 摄氏度 2. 华氏度")
    private String tempUnit;


    //http://192.168.31.215:8765/api/patient/central/takePictures
    //http://192.168.31.221:8108/central/takePictures
}
