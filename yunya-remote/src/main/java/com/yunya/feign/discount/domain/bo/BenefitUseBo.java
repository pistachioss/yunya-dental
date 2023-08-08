package com.yunya.feign.discount.domain.bo;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author xiangyang
 * @date 2020/9/15
 */
@Data
public class BenefitUseBo {
    private List<PatientUseBenefitBo> exchangeBenefitBos;
    private List<PatientUseBenefitBo> packageBenefitBos;
    private PatientUseBenefitBo discountBenefitBos;
    private PatientUseBenefitBo memberBenefitBo;
    private List<PatientUseBenefitBo> voucherBenefitBos;
    private List<PatientUseBenefitBo> deductionBenefitBos;
    public static BenefitUseBo getInstance() {
        BenefitUseBo benefitBo = new BenefitUseBo();
        benefitBo.setExchangeBenefitBos(Lists.newArrayList());
        benefitBo.setPackageBenefitBos(Lists.newArrayList());
        benefitBo.setVoucherBenefitBos(Lists.newArrayList());
        benefitBo.setDeductionBenefitBos(Lists.newArrayList());
        return benefitBo;
    }

}
