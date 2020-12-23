package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: App端账单项目信息视图模型
 * @author: LHB
 * @create: 2020-12-16 11:06
 **/
@Data
@ApiModel(value = "TreatmentOrderInfo4AppVO", description = "App端账单项目信息视图模型")
public class TreatmentOrderItem4AppVO implements Serializable {
    @ApiModelProperty("开单项目ID")
    private Integer billingItemId;
    @ApiModelProperty("开单项目名称")
    private String billingItemName;
    @ApiModelProperty("开单类型（0-价目表；1-商品；）")
    private Integer type;
    @ApiModelProperty("开单项目数量")
    private Integer quantity;
}
