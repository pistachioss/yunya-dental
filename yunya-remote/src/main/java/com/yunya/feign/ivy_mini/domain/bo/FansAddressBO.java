package com.yunya.feign.ivy_mini.domain.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class FansAddressBO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer fansId;
    private String name;
    private String phoneNumber;
    private String postCode;
    private String province;
    private String city;
    private String region;
    private String detailAddress;
    private Boolean defaultStatus;
    private String tag;
}
