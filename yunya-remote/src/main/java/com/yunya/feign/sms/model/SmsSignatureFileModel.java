package com.yunya.feign.sms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：短信签名的资质证明文件
 *
 * @author: chenlin
 * @Description: 短信签名的资质证明文件
 * @Date: 2020/12/11 14:37
 * @since: 1.0.0
 */
@ApiModel("短信签名的资质证明文件")
@ToString
@Data
public class SmsSignatureFileModel implements Serializable {
    /**
     * 文件格式：jpg、png、gif、jpeg
     */
    @ApiModelProperty("文件格式：jpg、png、gif、jpeg")
    private String fileType;

    /**
     * 文件存储位置
     */
    @ApiModelProperty("文件存储位置")
    private String fileUrl;

    /**
     * 文件内容
     */
    @ApiModelProperty("文件内容")
    private String fileContent;
}
