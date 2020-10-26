package com.yunya.models.discount;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "coupon_file_info")
public class CouponFileInfo {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 卡券公共信息ID
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 文件类型（0-图片；1-文档）
     */
    @Column(name = "file_type")
    private Byte fileType;
    /**
     * 文件名
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 文件地址
     */
    private String path;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;


}