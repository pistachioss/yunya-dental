package com.yunya.feign.discount.domain.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author xiangyang
 * @date 2020/9/17
 */
@Getter
@Setter
public class UseClinicBo {
    private Integer couponId;
    private String useClinicIdStr;
    private Integer couponName;
    private Integer limitCount;
}
