package com.yunya.feign.discount.factory;

import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.form.OwnCardActiveForm;
import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.model.AuthDiscountBenefitModel;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.discount.domain.vo.OrderBenefitDetailVo;
import com.yunya.feign.discount.domain.vo.PatientOrderBenefitVo;
import com.yunya.framework.common.model.ResponseResult;

import java.util.List;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
public class RemoteDiscountFallBackFactory implements RemoteDiscountFeign {
    @Override
    public ResponseResult ownActiveCard(Integer patientId,  OwnCardActiveForm form) {
        return null;
    }

    @Override
    public ResponseResult<PatientOrderBenefitVo> choiceBenefit(PatientChooseBenefitForm form) {
        return null;
    }

    @Override
    public ResponseResult saveCardBenefit(PatientOrderBenefitModel model) {
        return null;
    }

    @Override
    public ResponseResult saveAuthBenefit(AuthDiscountBenefitModel model) {
        return null;
    }

    @Override
    public List<OrderBenefitDetailVo> getOrderBenefitD(Integer orderId) {
        return null;
    }

    @Override
    public ResponseResult revokeBenefit(Integer orderId) {
        return null;
    }
}
