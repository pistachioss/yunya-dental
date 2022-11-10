package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/12
 * @description:
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "专家介绍VO")
public class ExpertIntroductionVO {

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "称谓")
    private String appellation;

    /**
     * 资质荣誉
     */
    @ApiModelProperty(value = "资质荣誉")
    private String honor;

    /**
     * 专业擅长
     */
    @ApiModelProperty(value = "专业擅长")
    private String expertise;

    /**
     * 专家形象图片地址
     */
    @ApiModelProperty(value = "家形象图片地址")
    private String pictureAddress;

    @ApiModelProperty(value = "出诊门诊数组")
    private Integer[] visitClinicIds;
    @ApiModelProperty(value = "出诊门诊")
    private String visitClinic;
    @ApiModelProperty(value = "出诊时间")
    private String visitTime;

    @ApiModelProperty(value = "科室Id数组")
    private Integer[] typeIds;
    @ApiModelProperty(value = "科室id（后端使用）")
    private String typeId;
    @ApiModelProperty(value = "科室名称 逗号隔开")
    private String typeName;
    /**
     * 是否置顶 0否 1是
     */
    @ApiModelProperty(value = "是否置顶 0否 1是")
    private Integer isSort;
    @ApiModelProperty(value = "置顶排序")
    private Integer sortNum;
    /**
     * 发布状态 0否1是
     */
    @ApiModelProperty(value = "发布状态 0否1是")
    private Integer status;


    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;
}
