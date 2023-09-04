package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/4 13:22
 * @description: 价目组合明细数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("价目组合明细数据模型")
public class TariffPackageDetailVO implements Serializable {

    /** id */
    @ApiModelProperty("id")
    private Integer id;

    /** 项目类型：0-价目，1-商品 */
    @ApiModelProperty("项目类型：0-价目，1-商品")
    private Byte itemType;

    /** 项目id */
    @ApiModelProperty("项目id")
    private Integer itemId;

    /** 项目名称 */
    @ApiModelProperty("项目名称")
    private String itemName;

    /** 项目编号 */
    @ApiModelProperty("项目编号")
    private String itemNumber;
    
    /** 项目数量 */
    @ApiModelProperty("项目数量")
    private Integer quantity;

    /** 项目分类 */
    @ApiModelProperty("项目分类")
    private String categoryName;
}
