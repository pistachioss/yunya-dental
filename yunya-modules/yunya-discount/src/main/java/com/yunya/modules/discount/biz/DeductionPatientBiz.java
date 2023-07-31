package com.yunya.modules.discount.biz;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.domain.bo.PatientCardBo;
import com.yunya.feign.discount.domain.form.DeductionActiveForm;
import com.yunya.feign.discount.domain.form.DeductionChangeForm;
import com.yunya.feign.discount.domain.form.OwnCardActiveForm;
import com.yunya.feign.discount.domain.model.CardMemberRefundModel;
import com.yunya.feign.discount.domain.model.CardPaymentModel;
import com.yunya.feign.discount.domain.model.CardPrepaymentRefundModel;
import com.yunya.feign.discount.domain.model.DeductionRefundModel;
import com.yunya.feign.discount.domain.query.CouponRefundQuery;
import com.yunya.feign.discount.domain.query.DeductionOrderQuery;
import com.yunya.feign.discount.domain.query.DeductionPatientQuery;
import com.yunya.feign.discount.domain.vo.DeductionRefundPayVO;
import com.yunya.feign.discount.domain.vo.PatientDeductionBaseVO;
import com.yunya.feign.discount.domain.vo.PatientDeductionOrderVO;
import com.yunya.feign.discount.domain.vo.PatientRefundOrderVO;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.model.MemberBillRechargeModel;
import com.yunya.feign.patient_central.domain.model.PrepaidBillRechargeModel;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.discount.*;
import com.yunya.modules.discount.enums.CouponOrderError;
import com.yunya.modules.discount.enums.CouponTypeEnum;
import com.yunya.modules.discount.enums.DiscountError;
import com.yunya.modules.discount.enums.UseWayEnum;
import com.yunya.modules.discount.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.yunya.framework.common.constant.BusinessConstants.MINI_CARD_REMARK;
import static com.yunya.modules.discount.enums.CouponOrderError.ORDER_BILL_AMOUNT;
import static com.yunya.modules.discount.enums.CouponOrderError.ORDER_BILL_ERROR;
import static com.yunya.modules.discount.enums.TrueFalseEnum.TRUE;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @auther: xy
 * @date: 2023/6/26
 */
@Service
@Slf4j
public class DeductionPatientBiz {
    @Resource
    private CardMapper cardMapper;
    @Resource
    private SalesChannelMapper salesChannelMapper;
    @Resource
    private ProductTypeMapper productTypeMapper;
    @Resource
    private CouponOrderMapper couponOrderMapper;
    @Resource
    private CardBenefitMapper cardBenefitMapper;
    @Resource
    private CardBiz cardBiz;
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private RemotePatientCentralServiceFeign patientFeign;
    @Resource
    private CouponOrderBiz couponOrderBiz;
    @Resource
    private CouponBillBiz couponBillBiz;
    @Resource
    private CouponRefundMapper refundMapper;
    @Resource
    private CouponRefundDetailMapper refundDetailMapper;
    @Resource
    private CouponRefundPayMapper refundPayMapper;

    public List<PatientDeductionBaseVO> deductionList(DeductionPatientQuery query) {
        List<PatientCardBo> list = cardMapper.listPatientDeductionByParam(query);
        //对象转换
        return list.stream().map(this::patientCardBoConvertVo)
                .collect(toList());
    }

    private PatientDeductionBaseVO patientCardBoConvertVo(PatientCardBo bo) {
        PatientDeductionBaseVO ownCardVo = BeanCopierUtils.generalCopyBean(bo, PatientDeductionBaseVO.class);
        //查询销售渠道
        SalesChannel salesChannel = salesChannelMapper.selectByPrimaryKey(bo.getSaleChannelId());
        ownCardVo.setSaleChannelName(salesChannel == null ? null : salesChannel.getName());
        ownCardVo.setCouponTypeName(CouponTypeEnum.getValue(bo.getCouponType()));
        if (Objects.nonNull(bo.getBuyerId())) {
            ownCardVo.setBuyerName(patientFeign.findPatientInfoById(bo.getBuyerId()).getName());
        }
        if (Objects.nonNull(bo.getBuyerId()) && Objects.nonNull(bo.getSoldTarget())) {
            ownCardVo.setOwnName(patientFeign.findPatientInfoById(Integer.valueOf(bo.getSoldTarget())).getName());
        }
        ownCardVo.setActiveStatus(bo.getStatus() >= 2 ? 1 : 0);
        if (Objects.nonNull(bo.getCardOwner())) {
            ownCardVo.setActivePatient(patientFeign.findPatientInfoByIds(Lists.newArrayList(bo.getCardOwner())).get(0).getName());
        }
        ownCardVo.setUseStatus(bo.getStatus() >= 3 ? 1 : 0);
        //产品分类
        ProductType productType = productTypeMapper.selectByPrimaryKey(bo.getProductTypeId());
        ownCardVo.setProductTypeName(productType == null ? null : productType.getName());
        ownCardVo.setUseWayName(UseWayEnum.getValue(bo.getUseWay()));
        ownCardVo.setUseDeadline(bo.getUseDeadline() == null ? "永久有效" : bo.getUseDeadline());
        ownCardVo.setPayChannel(TRUE.getCode());
        ownCardVo.setChangeStatus(1);
        String remark = bo.getRemark();
        if (StringUtils.isNotBlank(remark)) {
            ownCardVo.setPayChannel(Objects.equals(MINI_CARD_REMARK, remark) ? 0 : 1);
            ownCardVo.setChangeStatus(Objects.equals(MINI_CARD_REMARK, remark) ? 0 : 1);
        }
        if (Objects.nonNull(bo.getBuyerId())) {
            ownCardVo.setPayChannel(2);
        }
        return ownCardVo;
    }

    public List<PatientDeductionOrderVO> orderList(DeductionOrderQuery query) {
        List<PatientDeductionOrderVO> list = couponOrderMapper.listPatientDeductionByParam(query);
        list.forEach(t -> {
            t.setOperateName(systemServiceFeign.findSysUserEmployeeInfoByUserId(Integer.valueOf(t.getOperateName())).getName());
            t.setOrgName(systemServiceFeign.findOrgInfoByOrgId(Integer.valueOf(t.getOrgName())).getAbbreviation());
        });
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public void active(DeductionActiveForm form) {
        OwnCardActiveForm activeForm = new OwnCardActiveForm();
        activeForm.setCardId(form.getCardId());
        ResponseResult responseResult = cardBiz.ownActiveCard(form.getPatientId(), activeForm);
        if (!Objects.equals(0, responseResult.getStatus())) {
            throw ClientServiceException.wrap(responseResult.getStatus(), responseResult.getMsg());
        }
    }

    public void cancelActive(DeductionActiveForm form) {
        Integer patientId = form.getPatientId();
        Integer cardId = form.getCardId();
        Card card = cardMapper.selectByPrimaryKey(cardId);
        if (card == null) {
            throw ClientServiceException.wrap(DiscountError.CARD_NOT_EXIST);
        }
        if (!patientId.equals(card.getPatientId())) {
            throw ClientServiceException.wrap(DiscountError.OTHER_CARD_NOT_ALLOW_DELETE);
        }
        int useCount = cardBenefitMapper.countCardUsed(cardId);
        if (useCount > 0) {
            throw ClientServiceException.wrap(DiscountError.CARD_IS_USED);
        }
        cancel(card);
    }

    public PatientRefundOrderVO refundDetail(CouponRefundQuery query) {
        Integer orderId = query.getOrderId();
        PatientRefundOrderVO refundOrderVO = new PatientRefundOrderVO();
        CouponOrder order = couponOrderBiz.getOrder(orderId);
        List<CouponOrderDetail> details = couponOrderBiz.listOrderDetail(orderId, null);
        int totalQuantity = details.stream().map(CouponOrderDetail::getQuantity).reduce(0, Integer::sum);
        CouponOrderDetail detail = details.get(0);
        refundOrderVO.setOrderDetailId(detail.getId());
        refundOrderVO.setCouponName(detail.getCouponName());
        refundOrderVO.setPrice(detail.getPrice());
        refundOrderVO.setPackagePrice(detail.getReceivableAmount());
        refundOrderVO.setSaleAmount(detail.getReceivableAmount());
        refundOrderVO.setReceivedAmount(order.getReceivedAmount());
        refundOrderVO.setQuantity(1);
        refundOrderVO.setExecutorName(systemServiceFeign.findSysUserEmployeeInfoByUserId(detail.getExecutorId()).getName());
        refundOrderVO.setWholeRefund(false);
        if (totalQuantity == 1) {
            refundOrderVO.setWholeRefund(true);
        }
        List<CouponBillPayDetail> payDetails = couponBillBiz.listPayDetail(orderId);
        Map<Byte, List<CouponBillPayDetail>> collect =
                payDetails.stream().collect(groupingBy(CouponBillPayDetail::getType, toList()));

        collect.forEach((k, v) -> {
            if (Objects.equals(k, 0)) {
                List<DeductionRefundPayVO> collect1 = v.stream().map(t1 -> {
                    DeductionRefundPayVO prePay = new DeductionRefundPayVO();
                    prePay.setNumber(t1.getPatientNum());
                    return prePay;
                }).collect(toList());
                refundOrderVO.setPrePayment(collect1);
            }
            if (Objects.equals(k, 1)) {
                DeductionRefundPayVO member = new DeductionRefundPayVO();
                member.setNumber(v.get(0).getPatientNum());
                refundOrderVO.setMemberPay(member);
            }
        });
        return refundOrderVO;
    }

    public void refund(DeductionRefundModel model) {
        Integer orderId = model.getOrderId();
        Integer cardId = model.getCardId();
        CouponOrderVirtual virtual = couponOrderBiz.listOrderVirtual(orderId, cardId).get(0);
        List<CouponOrderDetail> details = couponOrderBiz.listOrderDetail(orderId, null);
        CouponOrderDetail detail = details.stream().filter(t -> Objects.equals(t.getId(), model.getOrderDetailId())).findFirst().orElse(null);
        if (Objects.isNull(detail)) {
            log.info("该订单礼包无法退费");
            return;
        }

        Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
        // 检查是否收费
        CouponBill bill = checkBillRecord(orderId);
        CardMemberRefundModel memberRefundModel = model.getMemberRefundModel();
        List<CardPrepaymentRefundModel> prepaymentRefundModel = model.getPrepaymentRefundModel();
        List<CardPaymentModel> refundPaymentModels = model.getRefundPaymentModels();
        String refundReason = model.getRefundReason();
        List<String> refundAnnex = model.getRefundAnnex();
        Integer billRecordId = bill.getId();
        Integer patientId = bill.getPatientId();
        Integer orderRecordId = bill.getOrderRecordId();
        // 计算退费开单总额
        BigDecimal refundOrderDetailAmount = virtual.getPackageUnitPrice();
        // 计算退费总额
        BigDecimal refundTotalAmount = model.getRefundAmount();
        if (refundOrderDetailAmount.compareTo(refundTotalAmount) != 0) {
            throw ClientServiceException.wrap(ORDER_BILL_AMOUNT);
        }
        String name = BaseContextHandler.getName();
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        // 保存退费记录
        CouponRefund couponRefund = new CouponRefund();
        couponRefund.setPatientId(patientId);
        couponRefund.setOrgId(orgId);
        couponRefund.setOrderId(orderRecordId);
        couponRefund.setRefundAmount(refundTotalAmount);
        couponRefund.setReason(refundReason);
        Joiner joiner = Joiner.on(",");
        if (StringHelper.isNotEmpty(refundAnnex)) {
            couponRefund.setRefundCertificate(joiner.join(refundAnnex));
        }
        couponRefund.setCrtId(userId);
        couponRefund.setUpdId(userId);
        int result = refundMapper.insertSelective(couponRefund);
        // 保存退费开单明细
        Integer refundId = couponRefund.getId();
        CouponRefundDetail refundDetail = new CouponRefundDetail();
        refundDetail.setOrgId(orgId);
        refundDetail.setOrderDetailId(detail.getId());
        refundDetail.setOrderVirtualId(virtual.getId());
        refundDetail.setCardId(virtual.getCardId());
        refundDetail.setRefundId(refundId);
        refundDetail.setRefundAmount(refundTotalAmount);
        refundDetail.setCrtId(userId);
        refundDetail.setUpdId(userId);
        // 保存账单退费付款明细记录
        saveBillRefundPayDetailRecord(
                orderRecordId,
                refundId,
                memberRefundModel,
                prepaymentRefundModel,
                refundPaymentModels, refundTotalAmount);
    }

    private List<Card> listCard(Collection<Integer> cardIds) {
        Example example = new Example(Card.class);
        example.createCriteria().andIn("id", cardIds);
        return cardMapper.selectByExample(example);
    }

    public void cancel(Card card) {
        card.setStatus(1);
        card.setPatientId(null);
        card.setActiveOrgId(null);
        card.setActiveUserId(null);
        card.setSharer(null);
        card.setActiveDate(null);
        cardMapper.updateByPrimaryKey(card);
    }

    public void change(DeductionChangeForm form) {
        Integer cardId = form.getCardId();
        Card card = cardMapper.selectByPrimaryKey(cardId);
        if (card == null) {
            throw ClientServiceException.wrap(DiscountError.CARD_NOT_EXIST);
        }
        Integer status = card.getStatus();
        if (Objects.equals(MINI_CARD_REMARK, card.getRemark())) {
            throw ClientServiceException.wrap(CouponOrderError.CHANGE_ERROR);
        }
        if (status >= 2) {
            throw ClientServiceException.wrap(CouponOrderError.CARD_ACTIVED);
        }
        int useCount = cardBenefitMapper.countCardUsed(cardId);
        if (useCount > 0) {
            throw ClientServiceException.wrap(DiscountError.CARD_IS_USED);
        }
        change(card, form);
    }

    public void change(Card card, DeductionChangeForm form) {
        card.setSoldTarget(form.getPatientId().toString());
        card.setSoldPhoneNumber(form.getMobile());
        cardMapper.updateByPrimaryKey(card);
    }

    private CouponBill checkBillRecord(Integer orderId) {
        CouponBill bill = couponBillBiz.getBill(orderId);
        if (null == bill) {
            throw ClientServiceException.wrap(ORDER_BILL_ERROR);
        }
        return bill;
    }

    private void saveBillRefundPayDetailRecord(
            Integer orderRecordId,
            Integer refundId,
            CardMemberRefundModel memberRefundModel,
            List<CardPrepaymentRefundModel> prepaymentRefundModels,
            List<CardPaymentModel> refundPaymentModels, BigDecimal refundTotalAmount) {
        CouponRefundPay refundPay = new CouponRefundPay();
        refundPay.setRefundId(refundId);
        refundPay.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        refundPay.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        // 会员费退费
        if (null != memberRefundModel) {
            refundPay.setAccountItemId(memberRefundModel.getAccountItemId());
            refundPay.setAccountItemName("会员卡");
            String memberNum = memberRefundModel.getMemberAccountId();
            refundPay.setPatientNumber(memberNum);
            BigDecimal principalAmount = memberRefundModel.getPrincipalAmount();
            BigDecimal giftAmount = memberRefundModel.getGiftAmount();
            if (principalAmount == null) {
                principalAmount = new BigDecimal(0);
            }
            if (giftAmount == null) {
                giftAmount = new BigDecimal(0);
            }
            refundPay.setRefundPayAmount(principalAmount.add(giftAmount));
            refundPay.setPrincipalAmount(principalAmount);
            refundPay.setGiftAmount(giftAmount);
            refundPay.setTotalAmount(refundPay.getTotalAmount());
            refundPayMapper.insertSelective(refundPay);
            // 会员卡退费金额返还
            MemberBillRechargeModel memberModel = new MemberBillRechargeModel();
            memberModel.setOrderRecordId(orderRecordId);
            memberModel.setMemberId(memberNum);
            memberModel.setRechargePrincipal(principalAmount);
            memberModel.setRechargeBonus(giftAmount);
            memberModel.setBillPayRecordId(refundId);
            memberModel.setRemarks(refundId.toString());
            patientFeign.billRefund(memberModel);
        }
        // 预付款退费
        if (StringHelper.isNotEmpty(prepaymentRefundModels)) {
            prepaymentRefundModels.forEach(prepaymentRefundModel -> {
                refundPay.setAccountItemId(prepaymentRefundModel.getAccountItemId());
                refundPay.setAccountItemName("预付款");
                String prepaymentNum = prepaymentRefundModel.getPrepaymentAccountId();
                refundPay.setPatientNumber(prepaymentNum);
                BigDecimal principalAmount = prepaymentRefundModel.getPrincipalAmount();
                BigDecimal giftAmount = prepaymentRefundModel.getGiftAmount();
                if (principalAmount == null) {
                    principalAmount = new BigDecimal(0);
                }
                if (giftAmount == null) {
                    giftAmount = new BigDecimal(0);
                }
                refundPay.setRefundPayAmount(principalAmount.add(giftAmount));
                refundPay.setPrincipalAmount(principalAmount);
                refundPay.setGiftAmount(giftAmount);
                refundPay.setTotalAmount(refundTotalAmount);
                refundPayMapper.insertSelective(refundPay);
                // 预付款退费金额返还
                PrepaidBillRechargeModel prepaidModel = new PrepaidBillRechargeModel();
                prepaidModel.setOrderRecordId(orderRecordId);
                prepaidModel.setPrepaidId(prepaymentNum);
                prepaidModel.setRechargePrincipal(principalAmount);
                prepaidModel.setRechargeBonus(giftAmount);
                prepaidModel.setBillPayRecordId(refundId);
                prepaidModel.setRemarks(refundId.toString());
                patientFeign.billRefund(prepaidModel);
            });
        }
        // 其它方式退款
        if (StringHelper.isNotEmpty(refundPaymentModels)) {
            refundPaymentModels.forEach(
                    paymentModel -> {
                        refundPay.setAccountItemId(paymentModel.getAccountItemId());
                        refundPay.setAccountItemName(paymentModel.getAccountItemName());
                        BigDecimal amount = paymentModel.getAmount();
                        refundPay.setRefundPayAmount(amount);
                        refundPay.setPrincipalAmount(amount);
                        refundPay.setTotalAmount(refundTotalAmount);
                        refundPay.setGiftAmount(null);
                        refundPayMapper.insertSelective(refundPay);
                    });
        }
    }
}
