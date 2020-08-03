package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 患者信息查询QueryFrom
 *
 * @author: WY
 * @date 2020/7/27 10:06
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者信息查询参数模型")
public class PatientBaseInfoQueryForm implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "患者id")
    private Integer id;

    /**
     * 患者姓名 字符串，长度64
     */
    @ApiModelProperty(value = "患者姓名", required = true)
    @NotNull(message = "患者姓名不能为空！")
    private String name;

    /**
     * 拼音姓名 字符串，长度64
     */
    @ApiModelProperty(value = "拼音姓名")
    private String pinyinName;

    /**
     * 手机号码 长度14
     */
    @ApiModelProperty(value = "患者手机号", required = true)
    @NotNull(message = "患者手机号不能为空！")
    private String mobile;

}
