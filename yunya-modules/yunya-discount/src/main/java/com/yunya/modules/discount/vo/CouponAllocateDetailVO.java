package com.yunya.modules.discount.vo;

import lombok.Data;

/**
 * @author 杨柳絮
 * @className CouponAllocateDetailVO
 * @description
 * @date 2020/8/21 13:00
 */
@Data
public class CouponAllocateDetailVO {

    private Integer id;
    /**
     * 配给对象ID
     */
    private Integer orgId;
    /**
     * 配给对象
     */
    private String orgName;

    /**
     * 配给数量
     */
    private Integer allocateNum;
}
