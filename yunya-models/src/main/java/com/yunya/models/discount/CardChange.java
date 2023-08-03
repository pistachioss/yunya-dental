package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 
 * 优惠卡
 */
@Data
@Table(name = "card_change")
public class CardChange implements Serializable {

    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "card_id")
    private Integer cardId;

    @Column(name = "buyer_id")
    private Integer buyerId;

    @Column(name = "pre_id")
    private Integer preId;

    private String remark;

    @Column(name = "curr_id")
    private Integer currId;

    @Column(name = "crt_id")
    private Integer crtId;

    @Column(name = "crt_time")
    private LocalDateTime crtTime;
}