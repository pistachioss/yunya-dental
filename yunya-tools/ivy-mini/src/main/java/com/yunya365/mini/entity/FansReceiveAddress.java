package com.yunya365.mini.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户收货地址表
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-20
 */
@Getter
@Setter
@TableName("fans_receive_address")
public class FansReceiveAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 微信用户id
     */
    @TableField("fans_id")
    private Integer fansId;

    /**
     * 收货人名称
     */
    @TableField("name")
    private String name;

    /**
     * 收货人电话
     */
    @TableField("phone_number")
    private String phoneNumber;

    /**
     * 收货人邮编
     */
    @TableField("post_code")
    private String postCode;

    /**
     * 省份/直辖市
     */
    @TableField("province")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 区
     */
    @TableField("region")
    private String region;

    /**
     * 详细地址(街道)
     */
    @TableField("detail_address")
    private String detailAddress;

    /**
     * 是否为默认
     */
    @TableField("default_status")
    private Boolean defaultStatus;

    /**
     * 标签
     */
    @TableField("tag")
    private String tag;

    /**
     * 创建时间
     */
    @TableField("crt_time")
    private LocalDateTime crtTime;

    @TableField("upd_time")
    private LocalDateTime updTime;


}
