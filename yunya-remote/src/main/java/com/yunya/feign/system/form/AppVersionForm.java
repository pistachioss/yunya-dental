package com.yunya.feign.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Value;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: APP端版本控制器表单
 * @author: LHB
 * @create: 2021-03-09 16:36
 **/
@ApiModel(value = "AppVersionForm", description = "APP端版本控制器表单")
@Data
public class AppVersionForm implements Serializable {
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

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人",required = true)
    @NotBlank(message = "创建人不能为空")
    private String crtName;

    /**
     * 创建日期
     */
    @ApiModelProperty(value = "创建日期",required = true)
    @NotNull(message = "创建日期不能为空")
    private Date crtTime;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateName;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
