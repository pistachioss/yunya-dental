package com.yunya.feign.patient_central.domain.model.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 简介:
 *
 * @author: YK
 * @date: 2020/9/21 10:40
 * @description: app端患者信息添加模板
 * @since: 1.0.0
 */
@Data
@ApiModel(value = "app端患者信息添加模板")
public class AppPatientBaseInfoModel implements Serializable {

    /**
     * 患者姓名 字符串，长度64
     */
    @NotNull(message = "患者名称为空！")
    @ApiModelProperty(value = "患者名称",required = true)
    private String name;

    /**
     * 手机号码 长度14
     */
    @NotBlank(message = "患者手机号不能为空")
    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式有误")
    @ApiModelProperty(value = "患者手机号码",required = true)
    private String mobile;

    /**
     * 手机号所属人 手机号所属人字典ID
     */
    @NotNull(message = "手机号所属人字典ID不能为空！")
    @ApiModelProperty(value = "手机号所属人字典ID",required = true)
    private Integer mobileOwner;
}