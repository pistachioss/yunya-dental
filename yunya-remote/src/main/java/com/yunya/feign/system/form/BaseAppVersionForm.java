package com.yunya.feign.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: APP端版本信息公共字段
 * @author: LHB
 * @create: 2021-03-10 15:54
 **/
@ApiModel(value = "BaseAppVersionForm",description = "APP端版本信息公共字段")
@Data
public class BaseAppVersionForm implements Serializable {
    /**
     * 设备系统(IOS,Android)
     */
    @ApiModelProperty(value = "设备系统(IOS,Android)")
    private String osName;

    /**
     * 当前最新版本号
     */
    @ApiModelProperty(value = "当前最新版本号")
    private String releaseVersion;

    /**
     * 内测版本号
     */
    @ApiModelProperty(value = "内测版本号")
    private String alphaVersion;

    /**
     * 正在公测的版本
     */
    @ApiModelProperty(value = "正在公测的版本")
    private String betaVersion;

    /**
     * 是否强制更新 0-不强制更新；1-强制更新
     */
    @ApiModelProperty(value = "是否强制更新 0-不强制更新；1-强制更新")
    private Boolean forceUpdate;

    /**
     * 下载地址
     */
    @ApiModelProperty(value = "下载地址")
    private String downloadUrl;

    /**
     * 更新内容
     */
    @ApiModelProperty(value = "更新内容")
    private String updateContent;
}
