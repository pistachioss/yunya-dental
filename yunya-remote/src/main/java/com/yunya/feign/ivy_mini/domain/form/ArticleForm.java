package com.yunya.feign.ivy_mini.domain.form;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/10
 * @description:
 */
@Data
@ApiModel(value = "文章管理")
public class ArticleForm extends PageQuery implements Serializable {

    @ApiModelProperty(value = "文章名称")
    private String name;

    /**
     * 分类 1：艾维动态 2口腔科普
     */
    @ApiModelProperty(value = "分类 1：艾维动态 2口腔科普")
    @NotNull(message = "分类不能为空！")
    private Integer type;

    /**
     * 发布状态 0否1是
     */
    @ApiModelProperty(value = "发布状态 0否1是")
    private Integer status;

    @ApiModelProperty(value = "排序字段 reading_number:按照阅读数/ crt_time：按照创建时间 可不传但不要空字符串")
    private String orderByColumn;

    /** 排序的方向desc或者asc */
    @ApiModelProperty(value = "排序的方向desc或者asc 可不传但不要空字符串")
    private String isAsc = "asc";


}
