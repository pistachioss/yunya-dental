package com.yunya365.mini.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单明细
 * </p>
 *
 * @author xiangyang
 * @since 2022-06-10
 */
@Getter
@Setter
@TableName("order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Integer orderId;

    /**
     * 订单号
     */
    @TableField("order_sn")
    private String orderSn;

    /**
     * 商品id
     */
    @TableField("product_id")
    private Integer productId;

    /**
     * 商品号码
     */
    @TableField("product_sn")
    private String productSn;

    @TableField("product_pic")
    private String productPic;

    /**
     * 商品价格
     */
    @TableField("product_price")
    private BigDecimal productPrice;

    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 购买数量
     */
    @TableField("product_quantity")
    private Integer productQuantity;

    /**
     * 商品分类id
     */
    @TableField("product_category_id")
    private Integer productCategoryId;

    /**
     * 商品分类名称
     */
    @TableField("product_category_name")
    private String productCategoryName;

    /**
     * 创建时间
     */
    @TableField("crt_time")
    private LocalDateTime crtTime;

    /**
     * 更新时间
     */
    @TableField("upd_time")
    private LocalDateTime updTime;


}
