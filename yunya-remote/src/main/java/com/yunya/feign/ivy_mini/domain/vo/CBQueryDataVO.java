package com.yunya.feign.ivy_mini.domain.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
public class CBQueryDataVO {
    @JSONField(name = "local_order_no")
    private String localOrderNo;
    @JSONField(name = "cb_order_no")
    private String cbOrderNo;
    @JSONField(name = "out_order_no")
    private String outOrderNo;
    @JSONField(name = "order_status")
    private String orderStatus;
    @JSONField(name = "total_amount")
    private long totalAmount;
    @JSONField(name = "receive_amount")
    private long receiveAmount;
    @JSONField(name = "refund_amount")
    private long refundAmount;
    @JSONField(name = "refund_time")
    private String refund_time;
    @JSONField(name = "payment_channel")
    private String paymentChannel;
    private String subject;
    private String remark;
    @JSONField(name = "discount_amount")
    private long discountAmount;
    @JSONField(name = "payment_way")
    private String paymentWay;
    @JSONField(name = "pay_time")
    private String payTime;
    @JSONField(name = "buyer_id")
    private String buyerId;
}
