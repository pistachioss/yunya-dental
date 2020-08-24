package com.yunya.feign.treatment_other.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 简介: 牙周期列表模型
 *
 * @author: chow
 * @date: 2020/8/11 15:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("添加牙根尖影像模块")
@Data
@ToString
public class ToothBitDataModel {

    @ApiModelProperty(value = "诊所id",required = true)
    @NotNull
    private int orgId;

    @ApiModelProperty(value = "患者id",required = true)
    @NotNull
    private int patientId;

    @ApiModelProperty(value = "就诊id",required = true)
    @NotNull
    private int treatmentRecordId;

    @ApiModelProperty(value = "牙医id",required = true)
    @NotNull
    private int dentistId;

    @ApiModelProperty(value = "图片上传日期",required = true)
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date uploadTime;

    @ApiModelProperty(value = "牙位id")
    @NotNull
    private Integer toothBit;

    @ApiModelProperty(value = "图片uri")
    @NotNull
    private String toothBitFilm;

    @ApiModelProperty(value = "图片名称")
    @NotNull
    private String filmName;
}