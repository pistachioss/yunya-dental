package com.yunya365.mini.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 购物车
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-27
 */
@Getter
@Setter
@TableName("cart_item")
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 微信用户id
     */
    @TableField("fans_id")
    private Integer fansId;

    @TableField("nick_name")
    private String nickName;

    /**
     * 商品id
     */
    @TableField("product_id")
    private Integer productId;

    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;

    @TableField("product_pic")
    private String productPic;

    /**
     * 购买数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 商品价格
     */
    @TableField("product_price")
    private BigDecimal productPrice;

    /**
     * 商品分类id
     */
    @TableField("product_category_id")
    private Integer productCategoryId;

    /**
     * 是否删除(0-否 1-是)
     */
    @TableField("delete_status")
    private String deleteStatus;

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
