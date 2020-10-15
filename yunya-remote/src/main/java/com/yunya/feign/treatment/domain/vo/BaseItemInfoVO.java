package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: yunya-dental
 * @description: 门诊价目表项目
 * @author: LHB
 * @create: 2020-10-15 15:14
 **/
@Data
@ToString
@ApiModel(value = "BaseItemInfoVO",description = "项目明细")
public class BaseItemInfoVO implements Serializable {
    /** 基础价目表ID */
    @ApiModelProperty(value = "基础价目表ID")
    private Integer id;

    /** 项目编码 */
    @ApiModelProperty(value = "项目编码 ")
    private String itemNumber;

    /** 项目名称 */
    @ApiModelProperty("项目名称")
    private String name;

    /** 单位 */
    @ApiModelProperty(value = "单位")
    private String unit;

    /** 拼音 */
    private String pinyin;

    /** 价格 */
    @ApiModelProperty(value = "价格")
    private BigDecimal price;
}
