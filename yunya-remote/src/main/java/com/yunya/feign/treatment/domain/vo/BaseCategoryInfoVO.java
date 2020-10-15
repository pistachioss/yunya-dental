package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 基础价目表(公司端)
 * @author: LHB
 * @create: 2020-10-15 14:40
 **/
@Data
@ApiModel(value = "BaseCategoryInfoVO",description = "基础价目表(公司端)")
public class BaseCategoryInfoVO implements Serializable {
    /** 分类项目 ID */
    @ApiModelProperty(value = "分类项目ID")
    private Integer categoryId;

    /** 商品分类名称 */
    @ApiModelProperty(value = "分类名称")
    private String categoryName;

    /** 商品分类编号 */
    @ApiModelProperty(value = "分类编号")
    private String categoryNumber;

    /** 基础价目表明细列表 */
    private List<BaseItemInfoVO> items;
}
