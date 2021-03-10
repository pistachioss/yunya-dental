package com.yunya.feign.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: APP端版本更新判断参数模型
 * @author: LHB
 * @create: 2021-03-09 16:36
 **/
@ApiModel(value = "AppVersionCheckForm", description = "APP端版本更新判断参数模型")
@Data
public class AppVersionCheckForm implements Serializable {
    /**
     * 设备系统(IOS,Android)
     */
    @ApiModelProperty(value = "设备系统(IOS,Android)",required = true)
    private String osName;

    /**
     * 当前正在运行的程序版本号
     */
    @ApiModelProperty(value = "当前正在运行的版本号",required = true)
    private String applicationVersion;

}
