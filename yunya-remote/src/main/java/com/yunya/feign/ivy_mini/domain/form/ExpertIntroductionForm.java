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
 * @date: 2022/5/12
 * @description:
 */
@Data
@ApiModel(value = "专家介绍")
public class ExpertIntroductionForm extends PageQuery implements Serializable {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "发布状态 0否1是")
    private Integer status;

    @ApiModelProperty(value = "排序的方向 desc表示置顶在上 asc表示不置顶在上 可不传但不要空字符串")
    private String isSort = "desc";

    @ApiModelProperty(value = "科室Ids")
    private Integer[] typeIds;

}
