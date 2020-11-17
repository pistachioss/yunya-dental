package com.yunya.feign.treatment_other.domain.form;

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
@ApiModel("修改图片影像模块")
@Data
@ToString
public class PhotoServiceForm {
    @ApiModelProperty(value = "主键id")
    @NotNull(message = "主键id不能为空")
    private Integer id;

    @ApiModelProperty(value = "图片上传日期",required = true)
    @NotNull(message = "图片上传日期不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date uploadTime;
}