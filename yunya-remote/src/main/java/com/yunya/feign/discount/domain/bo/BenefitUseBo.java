package com.yunya.feign.discount.domain.bo;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author xiangyang
 * @date 2020/9/15
 */
@Getter
@Setter
public class BenefitUseBo {
    private List<PatientUseBenefitBo> exchangeBenefitBos;
    private List<PatientUseBenefitBo> packageBenefitBos;
    private PatientUseBenefitBo discountBenefitBos;
    private PatientUseBenefitBo memberBenefitBo;
    private List<PatientUseBenefitBo> voucherBenefitBos;

    public static BenefitUseBo getInstance() {
        BenefitUseBo benefitBo = new BenefitUseBo();
        benefitBo.setExchangeBenefitBos(Lists.newArrayList());
        benefitBo.setPackageBenefitBos(Lists.newArrayList());
        benefitBo.setDiscountBenefitBos(new PatientUseBenefitBo());
        benefitBo.setVoucherBenefitBos(Lists.newArrayList());
        return benefitBo;
    }

}
