package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/12
 * @description:
 */
@Data
public class ExpertIntroductionAddAndUpdateForm {
    @ApiModelProperty(value = "Id")
    private Integer Id;
    @ApiModelProperty(value = "专家名称")
    @NotNull(message = "专家名称不能为空")
    private String name;
    @ApiModelProperty(value = "称谓")
    @NotNull(message = "称谓不能为空")
    private String appellation;
    @ApiModelProperty(value = "资质荣誉")
    private String honor;
    @ApiModelProperty(value = "专业擅长")
    private String expertise;
    @ApiModelProperty(value = "专家形象图片地址")
    private String pictureAddress;
    @ApiModelProperty(value = " 发布状态 0否1是")
    private Integer status;
    @ApiModelProperty(value = " 是否置顶 0否 1是")
    private Integer isSort;
    @ApiModelProperty(value = "分类Id")
    private Integer typeId;


}
