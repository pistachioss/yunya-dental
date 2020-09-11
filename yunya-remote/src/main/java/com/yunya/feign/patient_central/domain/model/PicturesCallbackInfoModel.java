package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 患者拍照信息回调
 *
 * @author: WY
 * @date 2020/9/10 14:35
 * @description: 患者拍照信息回调
 * @since: 1.0.0
 */
@Data
@ToString
public class PicturesCallbackInfoModel implements Serializable {

    @ApiModelProperty(value = "设备唯一标识码")
    private String deviceKey;

    @ApiModelProperty(value = "人员 ID ")
    private String personId;

    @ApiModelProperty(value = "时间戳")
    private String time;

    @ApiModelProperty(value = "照片路径（ftp 路径不完整，旧版参数）")
    private String imgPath;

    @ApiModelProperty(value = "照片路径（ftp 路径完整，新版参数）")
    private String newImgPath;

    @ApiModelProperty(value = "与 newImgPath 一致")
    private String path;

    @ApiModelProperty(value = "照片 id")
    private String faceId;

    @ApiModelProperty(value = "设备当前 IP 地址")
    private String ip;

    @ApiModelProperty(value = "特征码")
    private String feature;

    @ApiModelProperty(value = "特征秘钥，通过特征码注册时需要该字段进行特征有效性校验")
    private String featureKey;

    @ApiModelProperty(value = "设备算法版本")
    private String SDKVersion;

    @ApiModelProperty(value = "现场照 base64 码")
    private String base64;

}
