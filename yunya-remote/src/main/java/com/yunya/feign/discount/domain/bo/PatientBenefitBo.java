package com.yunya.feign.discount.domain.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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
    private String useDeadline;
    private Integer couponType;
    private Integer itemUsable;
    private String path;
    private Integer limitCount;

    public PatientBenefitBo() {
        this.itemUsable = 0;
    }

}
