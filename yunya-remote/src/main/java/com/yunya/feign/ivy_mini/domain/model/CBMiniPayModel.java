package com.yunya.feign.ivy_mini.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CBMiniPayModel extends CBPublicModel{
    @JSONField(name = "local_order_no")
    private String localOrderNo;
    private Long amount;
    private String remark;
    @JSONField(name = "goods_list")
    private String goodsList;
    @JSONField(name = "notify_url")
    private String notifyUrl;
    @JSONField(name = "payment_channel")
    private String paymentChannel;
    @JSONField(name = "sub_app_id")
    private String subAppId;
    @JSONField(name = "open_id")
    private String openId;
}
