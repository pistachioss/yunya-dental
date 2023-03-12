package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xiangyang
 * @date 2020/7/29
 */
@Setter
@Getter
@ApiModel(value = "病例模板查询模型")
public class MedicalTemplatePageVo {

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "sort")
    private Integer sort;

    @ApiModelProperty(value = "模板名称")
    private String name;

    @ApiModelProperty(value = "模板类型")
    private String type;

    @ApiModelProperty(value = "启用/禁用")
    private String enable;

}
