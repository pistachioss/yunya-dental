package com.yunya.feign.treatment_other.domain.vo;

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
@ApiModel("返回牙周期列表模型")
@Data
@ToString
public class PhotoServiceVo {

    @ApiModelProperty(value = "图片uri")
    private String uri;

    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "创建时间(当有修改上传时间时，则前端显示更新时间，否则一律显示创建时间)")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date crtTime;

    @ApiModelProperty(value = "更新时间(当有修改上传时间时，则前端显示更新时间，否则一律显示创建时间)")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date updTime;

    @ApiModelProperty(value = "图片上传日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date uploadTime;

    @ApiModelProperty(value = "图片名称")
    private String filmName;

}