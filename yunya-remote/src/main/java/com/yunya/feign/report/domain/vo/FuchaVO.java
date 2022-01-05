package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2021/12/30
 * @description:
 */
@Data
@ToString
@ApiModel("复查患者报表Vo")
public class FuchaVO implements Serializable {
    @Excel(name = "患者名称")
    @ApiModelProperty(value = "患者名称")
    private String name;
    /** 手机号 */
    @Excel(name = "手机号")
    @ApiModelProperty(value = "手机号")
    private String mobile;
    /** 初复诊 */
    @Excel(name = "初复诊", readConverterExp = "0=初诊,1=复诊")
    @ApiModelProperty(value = "初复诊")
    private Byte treatType;
    /** 末次接诊医生 */
    @Excel(name = "末次接诊医生")
    @ApiModelProperty(value = "末次接诊医生")
    private String employeeName;
    /** 末次就诊日期 */
    @Excel(name = "末次就诊日期")
    @ApiModelProperty(value = "末次就诊日期")
    private String lastVisitDate;
    @Excel(name = "门诊")
    @ApiModelProperty(value = "门诊")
    private String abbreviation;
}
