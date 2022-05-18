package com.yunya.feign.discount.factory;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.form.OwnCardActiveForm;
import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.model.AuthDiscountBenefitModel;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.discount.domain.query.DiscountCouponQuery;
import com.yunya.feign.discount.domain.vo.OrderBenefitDetailVo;
import com.yunya.feign.discount.domain.vo.PatientOrderBenefitVo;
import com.yunya.feign.discount.domain.vo.WxPatientEffectiveVo;
import com.yunya.feign.ivy_mini.domain.query.VirtualProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.VirtualProductVO;
import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.feign.report.domain.vo.WxCardUsageVo;
import com.yunya.feign.treatment.domain.vo.ClinicTariffDiscountCouponVO;
import com.yunya.framework.common.model.ResponseResult;

import java.math.BigDecimal;
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

    @Override
    public BigDecimal sumCardSoldAmount(CashReceiptOrRefundQuery query) {
        return null;
    }

    @Override
    public List<WxPatientEffectiveVo> getPatientEffectCardList(Integer patientId) {
        return null;
    }

    @Override
    public WxCardUsageVo getUserCardUsage(Integer cardId) {
        return null;
    }

    @Override
    public List<Integer> listPatientAllCard(Integer patientId) {
        return null;
    }

    @Override
    public List<ClinicTariffDiscountCouponVO> findClinicTariffCategoryDiscountCoupon(DiscountCouponQuery queryForm) {
        return null;
    }

    @Override
    public PageInfo<VirtualProductVO> pageVirtual(VirtualProductQuery query) {
        return null;
    }
}
