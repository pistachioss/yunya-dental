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
@ApiModel(value = "普通模板分页查询模型（词条，范句，要点，诊断）")
public class GeneralTemplatePageVo {

    private Integer id;
    @ApiModelProperty(value = "词条内容")
    private String content;
}
