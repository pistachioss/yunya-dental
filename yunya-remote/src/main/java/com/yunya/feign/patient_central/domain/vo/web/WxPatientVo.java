package com.yunya.feign.patient_central.domain.vo.web;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

/**
 * @description:
 * @author: xy
 * @date 2021/4/15 9:15
 **/
@Data
public class WxPatientVo {
    private String headImgUrl;
    private String userName;
    private String mobile;
    private Byte gender;
    @JsonFormat(pattern = "yyyy/MM/dd",timezone = "GMT+8")
    private String birthday;
    private String address;
    private String medicalHistory;
    private String allergen;
}
