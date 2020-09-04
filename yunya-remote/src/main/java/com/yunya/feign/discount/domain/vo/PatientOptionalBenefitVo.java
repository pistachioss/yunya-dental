package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.util.*;

/**
 * @author xiangyang
 * @date 2020/9/3
 */
@Getter
@Setter
@ApiModel(value = "患者可选优惠模型")
public class PatientOptionalBenefitVo {
    private List<MemberCardVo> memberCardList;
}
