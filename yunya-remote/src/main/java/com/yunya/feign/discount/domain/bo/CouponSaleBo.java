package com.yunya.feign.discount.domain.bo;

import lombok.*;

import java.io.*;
import java.math.*;

/**
 * @author xiangyang
 * @date 2020/8/25
 */
@Getter
@Setter
public class CouponSaleBo implements Serializable {
    private Integer couponId;
    private String couponName;
    private Integer couponType;
    private String saleSegment;
    private BigDecimal soldAmount;
    private Integer allocateNum;
    private Integer soldNum;
    private Integer orgId;
}
