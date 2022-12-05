package com.yunya.models.appointment;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
@Data
@Table(name = "reservation_limit_log")
public class ReservationLimitLog {
    @Id
    private Integer id;

    /**
     * 预约流量管理id
     */
    @Column(name = "res_config_id")
    private Integer resConfigId;

    /**
     * 操作(0-新增 1-修改)
     */
    private Byte operate;

    /**
     * 操作前数量
     */
    @Column(name = "operate_pre")
    private Integer operatePre;

    /**
     * 操作后数量
     */
    @Column(name = "operate_next")
    private Integer operateNext;

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

}