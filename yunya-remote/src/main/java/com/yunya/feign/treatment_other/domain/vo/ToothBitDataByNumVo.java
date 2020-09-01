package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 简介: 返回根尖片列表模型
 *
 * @author: chow
 * @date: 2020/8/11 15:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("返回根尖片首页缩影uri 统计")
@Data
@ToString
public class ToothBitDataByNumVo {
    @ApiModelProperty(value = "图片uri")
    private String toothBitFilm;

    @ApiModelProperty(value = "图片上传日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date uploadTime;

    @ApiModelProperty(value = "图片名称")
    private String filmName;

    @ApiModelProperty(value = "数量")
    private Integer num;

    @ApiModelProperty(value = "牙位id")
    private Integer toothBit;


}
