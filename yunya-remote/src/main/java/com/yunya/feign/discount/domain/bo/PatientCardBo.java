package com.yunya.feign.discount.domain.bo;

import lombok.*;

import java.io.*;
import java.time.*;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
@Getter
@Setter
public class PatientCardBo implements Serializable {
    private Integer cardId;
    private Integer couponId;
    private String couponName;
    private String cardNumber;
    private Integer couponType;
    private Integer productTypeId;
    private Integer saleChannelId;
    private Integer useWay;
    private String useDeadline;
    private LocalDate activeDate;
    private Integer share;
    private Integer cardOwner;
}
