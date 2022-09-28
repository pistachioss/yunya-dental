package com.yunya.models.system;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "qzt_sync_doctor")
@Data
public class QztSyncDoctor {
    @Id
    private Integer id;

    /**
     * 类型（0-医生 1-病例 2-项目）
     */
    private Integer type;

    /**
     * 最后一次id
     */
    @Column(name = "last_id")
    private Integer lastId;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;
}