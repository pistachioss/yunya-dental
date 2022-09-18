package com.yunya365.mini.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 虚拟卡券
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-15
 */
@Getter
@Setter
@Accessors(chain = true)
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
     * 订单号
     */
    @TableField("order_sn")
    private String orderSn;

    /**
     * 用户id
     */
    @TableField("fans_id")
    private Integer fansId;

    /**
     * 卡券id
     */
    @TableField("card_id")
    private Integer cardId;

    /**
     * 激活患者
     */
    @TableField("patient_id")
    private Integer patientId;

    /**
     * 患者姓名
     */
    @TableField("patient_name")
    private String patientName;

    /**
     * 激活手机
     */
    @TableField("active_mobile")
    private String activeMobile;

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
     * 删除状态：0->未删除；1->已删除
     */
    @TableField("delete_status")
    private Boolean deleteStatus;

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
