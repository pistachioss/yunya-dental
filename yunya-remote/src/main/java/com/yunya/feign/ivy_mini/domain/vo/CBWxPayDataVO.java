package com.yunya.feign.ivy_mini.domain.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
public class CBWxPayDataVO {
    @JSONField(name = "local_order_no")
    private String localOrderNo;
    @JSONField(name = "cb_order_no")
    private String cbOrderNo;
    @JSONField(name = "app_id")
    private String appId;
    private String timestamp;
    @JSONField(name = "nonce_str")
    private String nonceStr;
    @JSONField(name = "package")
    private String prepayId;
    @JSONField(name = "sign_type")
    private String signType;
    @JSONField(name = "pay_sign")
    private String paySign;
    private String subject;
    private String remark;

}
