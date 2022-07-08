package com.yunya365.mini.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单退货申请
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-04
 */
@Getter
@Setter
@TableName("order_return_apply")
public class OrderReturnApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Integer orderId;

    /**
     * 订单编号
     */
    @TableField("order_sn")
    private String orderSn;

    @TableField("out_order_no")
    private String outOrderNo;

    /**
     * 退款金额
     */
    @TableField("return_amount")
    private BigDecimal returnAmount;

    /**
     * 退货人姓名
     */
    @TableField("return_name")
    private String returnName;

    /**
     * 退货人电话
     */
    @TableField("return_phone")
    private String returnPhone;

    /**
     * 申请状态：0->待处理；1->退款；2->拒绝
     */
    @TableField("handle_status")
    private Integer handleStatus;

    /**
     * 微信退款状态：0-退款成功 1-退款失败
     */
    @TableField("refund_status")
    private Integer refundStatus;

    /**
     * 订单前状态(1->待发货；2->已发货；3->已完成)
     */
    @TableField("pre_status")
    private Integer preStatus;

    /**
     * 用户收货状态(0-未收到货 1-收到货)
     */
    @TableField("delivery_status")
    private Integer deliveryStatus;

    /**
     * 处理时间
     */
    @TableField("handle_time")
    private LocalDateTime handleTime;

    /**
     * 原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 处理备注
     */
    @TableField("handle_note")
    private String handleNote;

    /**
     * 处理人员
     */
    @TableField("handle_man")
    private String handleMan;

    /**
     * 申请时间
     */
    @TableField("crt_time")
    private LocalDateTime crtTime;

    /**
     * 更新时间
     */
    @TableField("upd_time")
    private LocalDateTime updTime;


}
