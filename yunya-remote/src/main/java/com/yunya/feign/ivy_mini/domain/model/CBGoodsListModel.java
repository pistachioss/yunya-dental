package com.yunya.feign.ivy_mini.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/6/22 10:06
 **/
@Data
public class CBGoodsListModel {
    @JSONField(name = "goods_id")
    private String goodsId;
    @JSONField(name = "goods_num")
    private String goodsNum;
    @JSONField(name = "goods_name")
    private String goodsName;
    @JSONField(name = "sell_amount")
    private String sellAmount;
    @JSONField(name = "goods_price")
    private String goodsPrice;
    @JSONField(name = "goods_sku_id")
    private String goodsSkuId;
}
