package com.yunya.feign.discount.domain.bo;

import lombok.*;

/**
 * @author xiangyang
 * @date 2020/8/20
 */
@Setter
@Getter
public class AllocateNumBo {

    private Integer orgId;
    private Integer couponAllocateId;
    private int startIndex;
    private int count;

    public static AllocateNumBo getInstance(){
        return new AllocateNumBo();
    }
}
