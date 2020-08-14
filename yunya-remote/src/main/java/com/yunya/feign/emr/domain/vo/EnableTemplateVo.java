package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

/**
 * @author xiangyang
 * @date 2020/8/11
 */
@Getter
@Setter
@ApiModel(value = "模板内容模型")
public class EnableTemplateVo {

    @ApiModelProperty(value = "模板id")
    private Integer id;

    @ApiModelProperty(value = "普通病例模板内容/病例模板名称")
    private String name;
}
