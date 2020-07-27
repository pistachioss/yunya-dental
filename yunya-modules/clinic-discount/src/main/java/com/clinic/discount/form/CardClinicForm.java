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
 * @create 2020-07-20 11:03
 */
@Data
@ApiModel("查询卡券分配表单")
public class CardClinicForm implements Serializable {
    @ApiModelProperty("产品名称")
    private String name;
    @ApiModelProperty("产品类型,0:代金券,1:折扣券,2:套餐券,4:充值卡")
    private List<Integer> types;
}
