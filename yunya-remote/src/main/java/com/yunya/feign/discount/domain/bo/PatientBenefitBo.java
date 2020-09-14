package com.yunya.feign.discount.domain.bo;

import lombok.*;

import java.io.*;
import java.time.*;

/**
 * @author xiangyang
 * @date 2020/9/9
 */
@Getter
@Setter
public class PatientBenefitBo implements Serializable {
    private Integer cardId;
    private Integer couponId;
    private String cardNumber;
    private Integer ownerId;
    private String couponName;
    private Integer mixable;
    private Integer useWay;
    private LocalDate useDeadline;
    private Integer couponType;
    private Integer itemUsable;
    private String path;

    public PatientBenefitBo() {
        this.itemUsable = 0;
    }

}
