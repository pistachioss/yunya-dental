package com.yunya365.mini.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-07
 */
@Getter
@Setter
@TableName("wx_pay_info")
public class WxPayInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "order_id")
    private Integer orderId;

    @TableField("app_id")
    private String appId;

    @TableField("time_stamp")
    private String timeStamp;

    @TableField("nonce_str")
    private String nonceStr;

    @TableField("package_value")
    private String packageValue;

    @TableField("sign_type")
    private String signType;

    @TableField("pay_sign")
    private String paySign;

    @TableField("crt_id")
    private Integer crtId;

    @TableField("crt_time")
    private LocalDateTime crtTime;


}
