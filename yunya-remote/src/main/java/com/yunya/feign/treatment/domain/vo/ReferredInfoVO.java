package com.yunya.feign.treatment.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.util.Date;

/**
 * 简介: 门诊端转诊记录VO
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
@ApiModel(value = "ReferredInfoVO",description = "转诊记录视图模型")
public class ReferredInfoVO {

    private Integer id;

    @ApiModelProperty("门诊id")
    private Integer orgId;

    @ApiModelProperty("患者id")
    private Integer patientId;

    @ApiModelProperty("患者名称")
    @ExcelProperty("患者名称")
    private String patientName;

    @ApiModelProperty("患者手机号")
    @ExcelProperty("患者手机号")
    private String telephone;

    @ApiModelProperty("转诊人id")
    private Integer userId;

    @ApiModelProperty("转诊人名称")
    @ExcelProperty("转诊人名称")
    private String userName;

    @ApiModelProperty("转诊人的科室ID")
    private Integer depId;

    @ApiModelProperty("转诊人的科室名称")
    @ExcelProperty("转诊人的科室名称")
    private String depName;

    @ApiModelProperty("转诊人挂号id")
    private Integer registeredId;

    @ApiModelProperty("被转诊人id")
    private Integer referredId;

    @ApiModelProperty("被转诊人名称")
    @ExcelProperty("被转诊人名称")
    private String referredName;

    @ApiModelProperty("被转诊人的科室ID")
    private Integer referredDepId;

    @ApiModelProperty("被转诊人的科室名称")
    @ExcelProperty("被转诊人的科室名称")
    private String referredDepName;

    @ApiModelProperty("被转诊人挂号id")
    private Integer referredRegisteredId;

    @ApiModelProperty("备注")
    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("转诊时间 （只有时分）")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "GMT+8")
    private Date time;

    @ApiModelProperty("创建人ID")
    private Integer crtId;

    @ApiModelProperty("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date crtTime;

    @ApiModelProperty("修改人ID")
    private Integer updId;

    @ApiModelProperty("修改时间")
    private Date updTime;


}
