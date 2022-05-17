package com.yunya365.mini.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-17
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 微信用户id
     */
    @TableField("fans_id")
    private Integer fansId;

    /**
     * 订单编号
     */
    @TableField("order_sn")
    private String orderSn;

    /**
     * 订单总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 应付金额（实际支付金额）
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 运费金额
     */
    @TableField("freight_amount")
    private BigDecimal freightAmount;

    /**
     * 优惠券抵扣金额
     */
    @TableField("coupon_amount")
    private BigDecimal couponAmount;

    /**
     * 支付方式：0->未支付；1->支付宝；2->微信
     */
    @TableField("pay_type")
    private Integer payType;

    /**
     * 订单来源：0->PC订单；1->小程序订单
     */
    @TableField("source_type")
    private Integer sourceType;

    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     */
    @TableField("status")
    private Integer status;

    /**
     * 订单类型：0->正常订单；1->秒杀订单；2-拼团订单
     */
    @TableField("order_type")
    private Integer orderType;

    /**
     * 物流公司(配送方式)
     */
    @TableField("delivery_company")
    private String deliveryCompany;

    /**
     * 物流单号
     */
    @TableField("delivery_sn")
    private String deliverySn;

    /**
     * 订单备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 确认收货状态：0->未确认；1->已确认
     */
    @TableField("confirm_status")
    private Integer confirmStatus;

    /**
     * 删除状态：0->未删除；1->已删除
     */
    @TableField("delete_status")
    private Integer deleteStatus;

    /**
     * 支付时间
     */
    @TableField("payment_time")
    private LocalDateTime paymentTime;

    /**
     * 发货时间
     */
    @TableField("delivery_time")
    private LocalDateTime deliveryTime;

    /**
     * 确认收货时间
     */
    @TableField("receive_time")
    private LocalDateTime receiveTime;

    /**
     * 评价时间
     */
    @TableField("comment_time")
    private LocalDateTime commentTime;

    /**
     * 修改时间
     */
    @TableField("modify_time")
    private LocalDateTime modifyTime;


}
