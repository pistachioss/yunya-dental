package com.clinic.discount.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-13 15:40
 */
@Data
@ApiModel("优惠项目表单")
public class DiscountDetailForm implements Serializable {
    @ApiModelProperty("价目表目录ID列表")
    private List<Integer> tariffCategoryIds;
    @ApiModelProperty("价目表ID列表")
    private List<DiscountDetailNode> tariffIds;
    @ApiModelProperty("商品目录ID列表")
    private List<Integer> oralTariffCategoryIds;
    @ApiModelProperty("商品ID列表")
    private List<DiscountDetailNode> oralTariffIds;
    @ApiModelProperty("优惠主体ID")
    private Integer id;
    @ApiModelProperty("类型，类型 0:代金券,1:折扣券,2:套餐券")
    private Integer type;
    @ApiModelProperty("范围,4的时候必传.代表全部范围")
    private Integer range;
}
