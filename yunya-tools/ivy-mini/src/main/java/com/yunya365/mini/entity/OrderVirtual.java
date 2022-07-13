package com.yunya365.mini.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 虚拟卡券
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-13
 */
@Getter
@Setter
@TableName("order_virtual")
public class OrderVirtual implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Integer orderId;

    /**
     * 用户id
     */
    @TableField("fans_id")
    private Integer fansId;

    /**
     * 卡券id(多张逗号分隔)
     */
    @TableField("card_id")
    private String cardId;

    /**
     * 激活患者
     */
    @TableField("patient_id")
    private Integer patientId;

    /**
     * 售卖手机
     */
    @TableField("sold_mobile")
    private String soldMobile;

    /**
     * 售卖时间
     */
    @TableField("sold_date")
    private LocalDateTime soldDate;

    /**
     * 激活时间
     */
    @TableField("active_date")
    private LocalDateTime activeDate;

    /**
     * 创建人
     */
    @TableField("crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @TableField("crt_time")
    private LocalDateTime crtTime;

    /**
     * 修改时间
     */
    @TableField("upd_time")
    private LocalDateTime updTime;

    /**
     * 更新人
     */
    @TableField("upd_id")
    private Integer updId;


}
