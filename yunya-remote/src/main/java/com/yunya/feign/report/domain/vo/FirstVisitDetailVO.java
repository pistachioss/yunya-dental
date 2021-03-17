package com.yunya.feign.report.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@ApiModel("初诊记录明细报表VO")
@Data
@ToString
public class FirstVisitDetailVO {

    @ApiModelProperty("门诊名称")
    @Excel(name = "门诊名称")
    private String orgName;
    @ApiModelProperty("初诊日期")
    @Excel(name = "初诊日期")
    private String firstVisitTime;
    @ApiModelProperty("患者姓名")
    @Excel(name = "患者姓名")
    private String patientName;
    @ApiModelProperty("手机号")
    @Excel(name = "手机号")
    private String mobile;
    @ApiModelProperty("患者来源分类")
    @Excel(name = "患者来源分类")
    private String originTypeName;
    @ApiModelProperty("患者来源")
    @Excel(name = "患者来源")
    private String originName;
}
