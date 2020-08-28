package com.yunya.feign.discount.domain.bo;

import lombok.*;

import java.io.*;
import java.time.*;

/**
 * @author xiangyang
 * @date 2020/8/19
 */
@Setter
@Getter
public class GenerateAllocatePageBo implements Serializable {
    private Integer couponId;
    private Integer couponAllocateId;
    private Integer submitUserId;
    private LocalDateTime submitDate;
    private String couponName;
    private Integer couponType;
    private Integer allocateNum;
    private Integer allocateUserId;
    private LocalDateTime allocateDate;
}
