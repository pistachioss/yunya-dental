package com.yunya.models.appointment;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "reservation_rate_limit")
public class ReservationRateLimit {
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 预约配置时间
     */
    @Column(name = "config_date")
    private Date configDate;

    /**
     * 当日配置可预约数量
     */
    @Column(name = "config_limit")
    private Integer configLimit;

    /**
     * 创建人名称
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人名称
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;
}