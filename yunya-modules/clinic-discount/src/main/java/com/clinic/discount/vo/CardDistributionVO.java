package com.clinic.discount.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-17 15:19
 */
@Data
public class CardDistributionVO implements Serializable {
    private Integer relevanceId;
    private Integer count;
    private String crtName;
    private Date crtTime;
    private Integer revision;
}
