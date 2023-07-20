package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.io.*;
import java.util.*;

/**
 * @author xiangyang
 * @date 2020/9/3
 */
@Data
@ApiModel(value = "患者可选优惠模型")
public class PatientOptionalBenefitVo implements Serializable {
    @ApiModelProperty(value = "会员卡模型")
    private List<PatientMemberCardVo> memberCardVoList;
    @ApiModelProperty(value = "折扣券模型")
    private List<PatientDiscountVo> discountVoList;
    @ApiModelProperty(value = "兑换券模型")
    private List<PatientExchangeVo> exchangeVoList;
    @ApiModelProperty(value = "套餐券模型")
    private List<PatientPackageVo> packageVoList;
    @ApiModelProperty(value = "代金券模型")
    private List<PatientVoucherVo> voucherVoList;
    @ApiModelProperty(value = "划扣券模型")
    private List<PatientPackageVo> deductionVoList;
}
