package com.clinic.discount.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-20 12:53
 */
@Data
public class CardStatisticsVO implements Serializable {
    /**
     * 关联优惠活动ID
     */
    private Integer relevanceId;
    /**
     * 关联优惠活动名称
     */
    private String relevanceName;
    /**
     * 总数
     */
    private Integer count;
    /**
     * 售出数量
     */
    private Integer sellingCount;
    /**
     * 类型 0:代金券,1:折扣券,2:套餐券,4:充值卡
     */
    private Integer type;

    /**
     * 0:分配计划中,1:完成分配
     */
    private Integer status;
    private String crtName;
    private Date crtTime;
    /**
     * 配给人
     */
    private String executorName;
    /**
     * 配给时间
     */
    private Date rationing_date;
    /**
     * 批次
     */
    private Integer revision;
}
