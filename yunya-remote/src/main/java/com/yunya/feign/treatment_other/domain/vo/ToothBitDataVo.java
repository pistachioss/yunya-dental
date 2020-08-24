package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 简介: 牙周期列表模型
 *
 * @author: chow
 * @date: 2020/8/11 15:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("返回牙周期列表模型")
@Data
@ToString
public class ToothBitDataVo {

    @ApiModelProperty(value = "图片uri")
    private String toothBitFilm;

    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "图片上传日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date uploadTime;

    @ApiModelProperty(value = "图片名称")
    private String filmName;

}