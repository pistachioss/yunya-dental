package com.yunya.modules.discount.biz;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.domain.bo.PatientCardBo;
import com.yunya.feign.discount.domain.form.DeductionActiveForm;
import com.yunya.feign.discount.domain.form.DeductionAllocateForm;
import com.yunya.feign.discount.domain.form.DeductionChangeForm;
import com.yunya.feign.discount.domain.form.OwnCardActiveForm;
import com.yunya.feign.discount.domain.model.CardMemberRefundModel;
import com.yunya.feign.discount.domain.model.CardPaymentModel;
import com.yunya.feign.discount.domain.model.CardPrepaymentRefundModel;
import com.yunya.feign.discount.domain.model.DeductionRefundModel;
import com.yunya.feign.discount.domain.query.CouponRefundQuery;
import com.yunya.feign.discount.domain.query.DeductionOrderQuery;
import com.yunya.feign.discount.domain.query.DeductionPatientQuery;
import com.yunya.feign.discount.domain.query.DeductionRecordQuery;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.model.MemberBillRechargeModel;
import com.yunya.feign.patient_central.domain.model.PrepaidBillRechargeModel;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.discount.*;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.modules.discount.enums.CouponOrderError;
import com.yunya.modules.discount.enums.CouponTypeEnum;
import com.yunya.modules.discount.enums.DiscountError;
import com.yunya.modules.discount.enums.UseWayEnum;
import com.yunya.modules.discount.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.modules.discount.enums.CouponOrderError.*;
import static com.yunya.modules.discount.enums.TrueFalseEnum.TRUE;
import static java.util.stream.Collectors.*;

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
    private CouponRefundPayMapper refundPayMapper;
    @Resource
    private CouponRefundDetailMapper refundDetailMapper;
    @Resource
    private CouponCommonInfoBiz couponBiz;
    @Resource
    private CardChangeMapper cardChangeMapper;
    @Resource
    private DeductionPeriodBiz deductionPeriodBiz;
    @Resource
    private SpecialPackageCouponItemBiz specialPackageCouponItemBiz;
    @Resource
    private CouponOrderVirtualMapper virtualMapper;
    @Resource
    private RemoteTreatmentServiceFeign treatmentServiceFeign;

    public PageInfo<PatientDeductionBaseVO> deductionList(DeductionPatientQuery query) {
        Page<PatientCardBo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        cardMapper.listPatientDeductionByParam(query);
        //对象转换
        List<PatientDeductionBaseVO> collect = page.getResult().stream().map(this::patientCardBoConvertVo)
                .collect(toList());
        PageInfo<PatientDeductionBaseVO> pageInfo = new PageInfo<>(collect);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        return pageInfo;
    }

    private PatientDeductionBaseVO patientCardBoConvertVo(PatientCardBo bo) {
        PatientDeductionBaseVO ownCardVo = BeanCopierUtils.generalCopyBean(bo, PatientDeductionBaseVO.class);
        //查询销售渠道
        SalesChannel salesChannel = salesChannelMapper.selectByPrimaryKey(bo.getSaleChannelId());
        ownCardVo.setSaleChannelName(salesChannel == null ? null : salesChannel.getName());
        ownCardVo.setCouponTypeName(CouponTypeEnum.getValue(bo.getCouponType()));
        String sharer = bo.getSharer();
        List<Integer> ids = Lists.newArrayList();
        List<Integer> collect11 = null;
        if (StringUtils.isNotBlank(sharer)) {
            collect11 = Arrays.stream(sharer.split(",")).map(Integer::parseInt).collect(toList());
            ids.addAll(collect11);
        }
        if (Objects.nonNull(bo.getBuyerId())) {
            ids.add(bo.getBuyerId());
        }
        if (Objects.nonNull(bo.getBuyerId()) && Objects.nonNull(bo.getSoldTarget())) {
            ids.add(Integer.valueOf(bo.getSoldTarget()));
        }
        ownCardVo.setActiveStatus(bo.getStatus() >= 2 ? 1 : 0);
        if (Objects.nonNull(bo.getCardOwner())) {
            ids.add(bo.getCardOwner());
        }
        List<PatientBaseInfoVo> patientInfos = patientFeign.findPatientInfoByIds(ids);
        if (CollectionUtils.isNotEmpty(patientInfos)) {
            Map<Integer, PatientBaseInfoVo> collect = patientInfos.stream().collect(toMap(PatientBaseInfoVo::getId, Function.identity(), (o, v) -> o));
            PatientBaseInfoVo p1 = collect.get(bo.getBuyerId());
            Integer ownId = Objects.nonNull(bo.getCardOwner()) ? bo.getCardOwner() :Integer.valueOf(bo.getSoldTarget());
            PatientBaseInfoVo p2 = collect.get(ownId);
            PatientBaseInfoVo p3 = collect.get(bo.getCardOwner());
            ownCardVo.setBuyerName(Objects.nonNull(p1) ? p1.getName(): null);
            ownCardVo.setOwnName(Objects.nonNull(p2) ? p2.getName(): null);
            ownCardVo.setActivePatient(Objects.nonNull(p3) ? p3.getName(): null);
            ownCardVo.setActivePatientMobile(Objects.nonNull(p3) ? p3.getMobile(): null);
            ownCardVo.setActivePatientId(bo.getCardOwner());
            if (CollectionUtils.isNotEmpty(collect11)) {
                String collect1 = collect11.stream()
                        .filter(collect::containsKey).map(t -> collect.get(t).getName()).collect(joining());
                ownCardVo.setSharer(collect1);
            }
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

    public PageInfo<PatientDeductionOrderVO> orderList(DeductionOrderQuery query) {
        Page<PatientDeductionOrderVO> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        couponOrderMapper.listPatientDeductionByParam(query);
        List<PatientDeductionOrderVO> result = page.getResult();
        result.forEach(t -> {
            t.setOperateName(systemServiceFeign.findSysUserEmployeeInfoByUserId(Integer.valueOf(t.getOperateName())).getName());
            t.setOrgName(systemServiceFeign.findOrgInfoByOrgId(Integer.valueOf(t.getOrgName())).getAbbreviation());
        });
        PageInfo<PatientDeductionOrderVO> pageInfo = new PageInfo<>(result);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        return pageInfo;
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

    @Transactional(rollbackFor = Exception.class)
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
        CouponBill bill = couponBillBiz.getBill(orderId);
        List<CouponOrderDetail> details = couponOrderBiz.listOrderDetail(orderId, query.getCouponId());
        CouponOrderDetail detail = details.get(0);
        Integer quantity = detail.getQuantity();
        refundOrderVO.setPatientId(bill.getPatientId());
        refundOrderVO.setOrderDetailId(detail.getId());
        refundOrderVO.setCouponName(detail.getCouponName());
        refundOrderVO.setPrice(detail.getPrice().divide(BigDecimal.valueOf(quantity), 4, RoundingMode.HALF_UP));
        refundOrderVO.setPackagePrice(detail.getReceivableAmount().divide(BigDecimal.valueOf(quantity), 4, RoundingMode.HALF_UP));
        refundOrderVO.setSaleAmount(detail.getReceivableAmount().divide(BigDecimal.valueOf(quantity), 4, RoundingMode.HALF_UP));
        refundOrderVO.setReceivedAmount(refundOrderVO.getSaleAmount());
        refundOrderVO.setTotalReceivedAmount(bill.getReceivedAmount());
        refundOrderVO.setQuantity(1);
        refundOrderVO.setExecutorName(systemServiceFeign.findSysUserEmployeeInfoByUserId(detail.getExecutorId()).getName());
        refundOrderVO.setWholeRefund(true);
        int useCount = cardBenefitMapper.countCardUsed(query.getCardId());
        if (useCount > 0) {
            refundOrderVO.setWholeRefund(false);
        }
        List<CouponBillPayDetail> payDetails = couponBillBiz.listPayDetail(orderId);
        Map<Byte, List<CouponBillPayDetail>> collect =
                payDetails.stream().collect(groupingBy(CouponBillPayDetail::getType, toList()));

        collect.forEach((k, v) -> {
            if (Objects.equals(k.intValue(), 0)) {
                List<DeductionRefundPayVO> collect1 = v.stream().map(t1 -> {
                    DeductionRefundPayVO prePay = new DeductionRefundPayVO();
                    prePay.setAccountItemId(t1.getAccountItemId());
                    prePay.setAmount(t1.getAmount());
                    prePay.setPrincipalAmount(t1.getPrincipalAmount());
                    prePay.setBonusAmount(t1.getBonusAmount());
                    prePay.setNumber(t1.getPatientNum());
                    return prePay;
                }).collect(toList());
                refundOrderVO.setPrePayment(collect1);
            }
            if (Objects.equals(k.intValue(), 1)) {
                DeductionRefundPayVO member = new DeductionRefundPayVO();
                CouponBillPayDetail t1 = v.get(0);
                member.setAccountItemId(t1.getAccountItemId());
                member.setAmount(t1.getAmount());
                member.setPrincipalAmount(t1.getPrincipalAmount());
                member.setBonusAmount(t1.getBonusAmount());
                member.setNumber(t1.getPatientNum());
                refundOrderVO.setMemberPay(member);
            }
            if (Objects.equals(k.intValue(), 2)) {
                List<DeductionRefundPayVO> collect1 = v.stream().map(t1 -> {
                    DeductionRefundPayVO payment = new DeductionRefundPayVO();
                    payment.setAccountItemId(t1.getAccountItemId());
                    payment.setAmount(t1.getAmount());
                    return payment;
                }).collect(toList());
                refundOrderVO.setPayment(collect1);
            }
        });
        return refundOrderVO;
    }
    @Transactional(rollbackFor = Exception.class)
    public void refund(DeductionRefundModel model) {
        Integer orderId = model.getOrderId();
        Integer cardId = model.getCardId();
        CouponOrder order = couponOrderBiz.getOrder(orderId);
        if (Objects.isNull(order) || order.getStatus() == 2) {
            throw ClientServiceException.wrap(ORDER_HAS_REFUND);
        }
        List<CouponOrderVirtual> virtuals = couponOrderBiz.listOrderVirtual(orderId, null, true);
        CouponOrderVirtual virtual = virtuals.stream().filter(t -> Objects.equals(t.getCardId(), cardId)).findFirst().orElse(null);
        if (Objects.isNull(virtual) || !virtual.getInservice()) {
            throw ClientServiceException.wrap(CARD_HAS_REFUND);
        }
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
        Integer patientId = bill.getPatientId();
        Integer orderRecordId = bill.getOrderRecordId();
        // 计算退费开单总额
        BigDecimal refundOrderDetailAmount = virtual.getPackageUnitPrice();
        // 计算退费总额
        BigDecimal refundTotalAmount = model.getRefundAmount();
        if (refundOrderDetailAmount.compareTo(refundTotalAmount) < 0) {
            throw ClientServiceException.wrap(ORDER_BILL_AMOUNT);
        }
        int useCount = cardBenefitMapper.countCardUsed(cardId);
        if (useCount <= 0) {
            if (refundOrderDetailAmount.compareTo(refundTotalAmount) != 0) {
                throw ClientServiceException.wrap(ORDER_BILL_REFUND_AMOUNT);
            }
        }
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
        refundDetailMapper.insertSelective(refundDetail);
        // 保存账单退费付款明细记录
        saveBillRefundPayDetailRecord(
                orderRecordId,
                refundId,
                memberRefundModel,
                prepaymentRefundModel,
                refundPaymentModels, refundTotalAmount);
        virtual.setInservice(false);
        couponOrderBiz.refundVirtual(details, detail, virtuals, virtual, order);
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

    @Transactional(rollbackFor = Exception.class)
    public void change(DeductionChangeForm form) {
        Integer cardId = form.getCardId();
        Card card = cardMapper.selectByPrimaryKey(cardId);
        if (card == null) {
            throw ClientServiceException.wrap(DiscountError.CARD_NOT_EXIST);
        }
        if (Objects.isNull(card.getBuyerId())) {
            throw ClientServiceException.wrap(CARD_CHANGE_ERROR);
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
        saveChange(card, form);
        change(card, form);
    }

    private void saveChange(Card card, DeductionChangeForm form) {
        CardChange cardChange = new CardChange();
        cardChange.setCardId(card.getId());
        cardChange.setBuyerId(card.getBuyerId());
        cardChange.setPreId(Integer.valueOf(card.getSoldTarget()));
        cardChange.setCurrId(form.getPatientId());
        cardChange.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        cardChange.setRemark(form.getRemark());
        cardChangeMapper.insertSelective(cardChange);
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
        // 会员费退费
        if (null != memberRefundModel) {
            CouponRefundPay refundPay = new CouponRefundPay();
            refundPay.setRefundId(refundId);
            refundPay.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
            refundPay.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
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
            refundPay.setTotalAmount(refundTotalAmount);
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
                CouponRefundPay refundPay = new CouponRefundPay();
                refundPay.setRefundId(refundId);
                refundPay.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                refundPay.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
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
                        CouponRefundPay refundPay = new CouponRefundPay();
                        refundPay.setRefundId(refundId);
                        refundPay.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                        refundPay.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
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

    public String allocate(DeductionAllocateForm form) {
        CouponCommonInfo commonInfo = couponBiz.selectById(form.getCouponId());
        List<Card> cards = cardMapper.listRemaining(Lists.newArrayList(form.getCouponId()), Integer.parseInt(BaseContextHandler.getOrgId()));
        if (CollectionUtils.isEmpty(cards)) {
            throw ClientServiceException.wrap(COUPON_STOCK_LACK, commonInfo.getName());
        }
        return cards.get(0).getCardNumber();
    }

    public PageInfo<DeductionRefundRecordVO> refundList(DeductionRecordQuery query) {
        Page<DeductionRefundRecordVO> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<DeductionRefundRecordVO> list = virtualMapper.refundList(query);
        log.info("退费记录分页类型：{},{}", list,list.getClass());
        if (CollectionUtils.isNotEmpty(list)) {
            List<OrganizationInfoDetail> orgInfoInIds = systemServiceFeign.findOrgInfoInIds(list.stream().map(t -> Integer.valueOf(t.getOrgName())).collect(toList()));
            Map<Integer, String> collect = orgInfoInIds.stream().collect(toMap(OrganizationInfoDetail::getId, OrganizationInfoDetail::getAbbreviation, (o, v) -> v));
            List<SysUserInfoDetail> users = systemServiceFeign.findSysUserEmployeeInfoByUserIds(list.stream().map(t -> Integer.valueOf(t.getExecutorName())).collect(toList()));
            Map<Integer, String> collect1 = users.stream().collect(toMap(SysUserInfoDetail::getUserId, SysUserInfoDetail::getName, (o, v) -> v));
            list.forEach(t -> {
                t.setOrgName(collect.get(Integer.valueOf(t.getOrgName())));
                t.setExecutorName(collect1.get(Integer.valueOf(t.getExecutorName())));
            });
        }
        return new PageInfo<>(list);
    }

    public PageInfo<DeductionUsedRecordVO> usedList(DeductionRecordQuery query) {
        Page<DeductionUsedRecordVO> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<DeductionUsedRecordVO> list = virtualMapper.usedList(query);
        log.info("消耗记录分页类型：{},{}", list,list.getClass());
        if (CollectionUtils.isNotEmpty(list)) {
            List<SysUserInfoDetail> users = systemServiceFeign.findSysUserEmployeeInfoByUserIds(list.stream().map(t -> Integer.valueOf(t.getExecutorName())).collect(toList()));
            Map<Integer, String> collect1 = users.stream().collect(toMap(SysUserInfoDetail::getUserId, SysUserInfoDetail::getName, (o, v) -> v));
            List<OrganizationInfoDetail> orgInfoInIds = systemServiceFeign.findOrgInfoInIds(list.stream().map(t -> Integer.valueOf(t.getOrgName())).collect(toList()));
            Map<Integer, String> collect = orgInfoInIds.stream().collect(toMap(OrganizationInfoDetail::getId, OrganizationInfoDetail::getAbbreviation, (o, v) -> v));
            List<Integer> collect3 = list.stream().flatMap(t -> Stream.of(Integer.valueOf(t.getPatientName()))).collect(toList());
            List<PatientBaseInfoVo> patientInfoByIds = patientFeign.findPatientInfoByIds(collect3);
            Map<Integer, String> collect2 = patientInfoByIds.stream().collect(toMap(PatientBaseInfoVo::getId, PatientBaseInfoVo::getName, (o, v) -> v));
            list.forEach(t -> {
                Integer orgId = Integer.valueOf(t.getOrgName());
                Integer itemId = Integer.valueOf(t.getItemName());
                t.setPatientName(collect2.get(Integer.valueOf(t.getPatientName())));
                t.setOrgName(collect.get(orgId));
                t.setExecutorName(collect1.get(Integer.valueOf(t.getExecutorName())));
                if (ZERO.equals(t.getItemType())) {
                    BaseTariff tariff = treatmentServiceFeign.findBaseTariffById(itemId);
                    t.setItemName(tariff.getName());
                }
                if (ONE.equals(t.getItemType())) {
                    BaseOralTariff tariff = treatmentServiceFeign.findBaseOralTariffById(itemId);
                    t.setItemName(tariff.getName());
                }
            });
        }
        return new PageInfo<>(list);
    }

    public PageInfo<DeductionChangeRecordVO> changeList(DeductionRecordQuery query) {
        Page<DeductionChangeRecordVO> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<DeductionChangeRecordVO> list = virtualMapper.changeList(query);
        log.info("赠与记录分页类型：{},{}", list,list.getClass());
        if (CollectionUtils.isNotEmpty(list)) {
            List<SysUserInfoDetail> users = systemServiceFeign.findSysUserEmployeeInfoByUserIds(list.stream().map(t -> Integer.valueOf(t.getExecutorName())).collect(toList()));
            Map<Integer, String> collect1 = users.stream().collect(toMap(SysUserInfoDetail::getUserId, SysUserInfoDetail::getName, (o, v) -> v));
            List<Integer> collect = list.stream().flatMap(t -> Stream.of(Integer.valueOf(t.getPatientName()), Integer.valueOf(t.getOwnName()))).collect(toList());
            List<PatientBaseInfoVo> patientInfoByIds = patientFeign.findPatientInfoByIds(collect);
            Map<Integer, String> collect2 = patientInfoByIds.stream().collect(toMap(PatientBaseInfoVo::getId, PatientBaseInfoVo::getName, (o, v) -> v));
            list.forEach(t -> {
                t.setPatientName(collect2.get(Integer.valueOf(t.getPatientName())));
                t.setOwnName(collect2.get(Integer.valueOf(t.getOwnName())));
                t.setExecutorName(collect1.get(Integer.valueOf(t.getExecutorName())));
            });
        }
        return new PageInfo<>(list);
    }

    public List<DeductionItemPeriodVO> itemList(Integer cardId) {
        Card card = cardMapper.selectByPrimaryKey(cardId);
        List<DeductionItemPeriod> periods = deductionPeriodBiz.listByCoupon(Lists.newArrayList(card.getCouponId()), DateUtil.localDateTimeToDate(Objects.isNull(card.getSoldDate())? card.getActiveDate() : card.getSoldDate()));
        if (CollectionUtils.isNotEmpty(periods)) {
            return BeanCopierUtils.listGeneralCopyBean(periods, DeductionItemPeriodVO.class);
        }
        SpecialPackageCouponItem specialPackageCouponItem = new SpecialPackageCouponItem();
        specialPackageCouponItem.setCouponId(card.getCouponId());
        List<SpecialPackageCouponItem> couponItems = specialPackageCouponItemBiz.selectList(specialPackageCouponItem);
        return BeanCopierUtils.listGeneralCopyBean(couponItems, DeductionItemPeriodVO.class);
    }

}
