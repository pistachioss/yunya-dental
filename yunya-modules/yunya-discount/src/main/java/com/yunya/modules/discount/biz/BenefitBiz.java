package com.yunya.modules.discount.biz;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yunya.feign.discount.domain.bo.BillUsedCouponBo;
import com.yunya.feign.discount.domain.bo.CardUseBo;
import com.yunya.feign.discount.domain.bo.ItemUseBenefitBo;
import com.yunya.feign.discount.domain.bo.OrderItemUseBo;
import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.model.AuthDiscountBenefitModel;
import com.yunya.feign.discount.domain.model.AuthItemBenefitModel;
import com.yunya.feign.discount.domain.model.MixMatchBenefitModel;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.discount.domain.query.DiscountCouponQuery;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.ClinicTariffDiscountCouponVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.ChoiceBenefitTypeEnum;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.discount.*;
import com.yunya.models.system.MemberType;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.modules.discount.enums.DiscountError;
import com.yunya.modules.discount.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseBenefit;
import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseCardSingle;
import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.framework.common.enums.ChoiceBenefitTypeEnum.*;
import static com.yunya.modules.discount.enums.BenefitOperateEnum.CHARGE;
import static com.yunya.modules.discount.enums.BenefitOperateEnum.MODIFY_BILL;
import static com.yunya.modules.discount.enums.BenefitTypeEnum.COUPON_TYPE;
import static com.yunya.modules.discount.enums.BenefitTypeEnum.MEMBER_TYPE;
import static com.yunya.modules.discount.enums.CardStatusEnum.*;
import static com.yunya.modules.discount.enums.CouponTypeEnum.*;
import static com.yunya.modules.discount.enums.TrueFalseEnum.FALSE;
import static com.yunya.modules.discount.enums.TrueFalseEnum.TRUE;
import static com.yunya.modules.discount.enums.UseWayEnum.ONE_TIME_USE;
import static java.util.stream.Collectors.*;

/**
 * @author xiangyang
 * @date 2020/10/9
 */
@Service
@Slf4j
public class BenefitBiz {

    private static final Integer AUTH_BENEFIT_TYPE = 2;
    @Resource
    private CardBiz cardBiz;
    @Resource
    private CardBenefitMapper cardBenefitMapper;
    @Resource
    private OrderBenefitMapper orderBenefitMapper;
    @Resource
    private AuthDiscountBenefitMapper authDiscountBenefitMapper;
    @Resource
    private VoucheCouponMapper voucherMapper;
    @Resource
    private DiscountCouponMapper discountCouponMapper;
    @Resource
    private PackageCouponItemMapper packageCouponItemMapper;
    @Resource
    private SpecialPackageCouponItemMapper specialPackageCouponItemMapper;
    @Resource
    private CouponCommonInfoMapper couponMapper;
    @Resource
    private CardMapper cardMapper;
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private RemoteTreatmentServiceFeign treatmentServiceFeign;
    @Resource
    private RemoteRabbitMqServiceFeign mqServiceFeign;
    @Resource(name = "customizeThreadPool")
    private ExecutorService cardThreadPool;
    @Resource
    private DeductionPeriodBiz periodBiz;
    @Resource
    private CouponOrderBiz orderBiz;

    /**
     * 收费 - 卡券保存优惠
     *
     * @param model model
     * @return ResponseResult
     */
    @Transactional
    public ResponseResult saveCardBenefit(PatientOrderBenefitModel model) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer orderId = model.getOrderId();
        try {
            //校验是否使用过优惠
            int count = countOrder(orderId);
            if (count != 0) {
                return ResponseUtil.error(DiscountError.ORDER_HAS_BENEFIT);
            }
            log.info("收费时选择的优惠券信息：[{}]", model);
            //设置门诊为开单门诊（重置组织id）
            this.setOrderField(model);
            PatientChooseBenefitForm benefitForm = benefitTransformToForm(model);
            //查询订单项目对应的优惠
            ResponseResult result = cardBiz.choiceBenefitBo(benefitForm);
            log.info("收费时选择的优惠券信息匹配优惠：{}", result);
            if (!FALSE.getCode().equals(result.getStatus())) {
                return result;
            }
            List<OrderItemUseBo> data = (List<OrderItemUseBo>) result.getData();
            List<CardBenefit> list = Lists.newArrayList();
            CardBenefit cardBenefit;
            for (OrderItemUseBo itemBenefitBo : data) {
                List<ItemUseBenefitBo> itemUseBenefitBos = itemBenefitBo.getItemUseBenefitBos();
                if (CollectionUtils.isNotEmpty(itemUseBenefitBos)) {
                    for (ItemUseBenefitBo itemUseBenefitBo : itemUseBenefitBos) {
                        cardBenefit = new CardBenefit();
                        cardBenefit.setOrgId(benefitForm.getOrgId());
                        cardBenefit.setOrderId(benefitForm.getOrderId());
                        cardBenefit.setOrderDetailId(itemBenefitBo.getOrderDetailId());
                        cardBenefit.setCouponId(itemUseBenefitBo.getCouponId());
                        cardBenefit.setPatientId(benefitForm.getPatientId());
                        cardBenefit.setCardId(itemUseBenefitBo.getBenefitId());
                        cardBenefit.setItemId(itemBenefitBo.getItemId());
                        cardBenefit.setItemType(itemBenefitBo.getType());
                        cardBenefit.setCouponType(itemUseBenefitBo.getCouponType());
                        cardBenefit.setBenefitType(itemUseBenefitBo.getBenefitType());
                        cardBenefit.setItemIndex(itemUseBenefitBo.getItemIndex());
                        cardBenefit.setBenefitAmount(itemUseBenefitBo.getBenefitAmount());
                        cardBenefit.setOperateType(CHARGE.getCode());
                        cardBenefit.setCrtId(loginUserId);
                        cardBenefit.setUpdId(loginUserId);
                        cardBenefit.setSort(itemUseBenefitBo.getId());
                        //计算工作量
                        cardBenefit.setSupplyWorkload(calculateWordLoad(itemUseBenefitBo,itemBenefitBo));
                        list.add(cardBenefit);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                BigDecimal totalBenefitAmount = data.stream()
                        .filter(obj -> obj.getBenefitAmount() != null).map(OrderItemUseBo::getBenefitAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                OrderBenefit orderBenefit = new OrderBenefit();
                orderBenefit.setOrderId(orderId);
                orderBenefit.setTotalAmount(totalBenefitAmount);
                orderBenefit.setBenefitType(CARD_BENEFIT.getCode());
                orderBenefit.setCrtId(loginUserId);
                orderBenefit.setUpdId(loginUserId);
                orderBenefitMapper.insertSelective(orderBenefit);
                cardBenefitMapper.insertList(list);
                //需要更新的卡券
                Set<Card> updateCards = updateCardStatus(list);
                mqServiceFeign.sendMessage(orderId, ADD, BaseBenefit);
                log.info("【订单使用卡券优惠发送消息成功】：订单id[{}]", orderId);
                updateCards.forEach(obj -> mqServiceFeign.sendMessage(obj.getId(), UPDATE, BaseCardSingle));
                log.info("【订单使用卡券优惠，更新卡券发送消息成功】：卡券ids：{}", updateCards.stream().map(Card::getId).collect(toList()));
            }
            orderBiz.occur(orderId, 2, model.getPatientId(), null, null);
            return ResponseUtil.success();
        } finally {
            //解锁卡券
            cardBiz.manualUnLock(loginUserId, RedisConstants.LOCK_CHOICE_CARD);
            log.info("【保存卡券优惠解锁成功】");
        }
    }

    private void setOrderField(PatientOrderBenefitModel model) {
        OrderRecord record = treatmentServiceFeign.findOrderRecordById(model.getOrderId());
        if (record != null) {
            model.setOrgId(record.getOrgId());
        }
    }

    /**
     * 收费 - 授权折扣优惠
     *
     * @param model model
     * @return ResponseResult
     */
    @Transactional
    public ResponseResult saveAuthBenefit(AuthDiscountBenefitModel model) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer orderId = model.getOrderId();
        try {
            //校验是否使用过优惠
            int count = countOrder(orderId);
            if (count != 0) {
                return ResponseUtil.error(DiscountError.ORDER_HAS_BENEFIT);
            }
            SysEmployee employee = systemServiceFeign.findSysEmployeeById(model.getAuthorizedId());
            if (employee == null || !employee.getDiscount()) {
                log.warn("【授权折扣优惠】授权人没有权限进行授权折扣");
                return ResponseUtil.error(DiscountError.EMPLOYEE_NO_AUTH_DISCOUNT);
            }
            //获取订单明细
            List<OrderDetailChargeVO> orderDetails = treatmentServiceFeign.findOrderDetailByOrderRecordId(orderId);
            if (CollectionUtils.isEmpty(orderDetails)) {
                return ResponseUtil.error(DiscountError.ORDER_NOT_EXIST);
            }
            List<AuthItemBenefitModel> itemBenefits = model.getItemBenefits();
            AuthDiscountBenefit authDiscountBenefit;
            List<AuthDiscountBenefit> list = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(itemBenefits)) {
                RestErrorBo errorBo = checkAuthItem(orderDetails, itemBenefits);
                if (errorBo.getError() != null) {
                    return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
                }
                for (AuthItemBenefitModel itemBenefit : itemBenefits) {
                    authDiscountBenefit = new AuthDiscountBenefit();
                    authDiscountBenefit.setOrgId(model.getOrgId());
                    authDiscountBenefit.setOrderId(orderId);
                    authDiscountBenefit.setOrderDetailId(itemBenefit.getOrderDetailId());
                    authDiscountBenefit.setPatientId(model.getPatientId());
                    authDiscountBenefit.setItemId(itemBenefit.getItemId());
                    authDiscountBenefit.setItemType(itemBenefit.getType());
                    authDiscountBenefit.setBenefitAmount(itemBenefit.getBenefitAmount());
                    authDiscountBenefit.setOperateType(CHARGE.getCode());
                    authDiscountBenefit.setCrtId(loginUserId);
                    authDiscountBenefit.setUpdId(loginUserId);
                    list.add(authDiscountBenefit);
                }
                if (CollectionUtils.isNotEmpty(list)) {
                    BigDecimal totalBenefitAmount = itemBenefits.stream().map(AuthItemBenefitModel::getBenefitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    OrderBenefit orderBenefit = new OrderBenefit();
                    orderBenefit.setOrderId(orderId);
                    orderBenefit.setTotalAmount(totalBenefitAmount);
                    orderBenefit.setBenefitType(AUTH_BENEFIT.getCode());
                    orderBenefit.setAuthorizedId(model.getAuthorizedId());
                    orderBenefit.setRemark(model.getRemark());
                    orderBenefit.setCrtId(loginUserId);
                    orderBenefit.setUpdId(loginUserId);
                    orderBenefitMapper.insertSelective(orderBenefit);
                    authDiscountBenefitMapper.insertList(list);
                    mqServiceFeign.sendMessage(orderId, ADD, BaseBenefit);
                    log.info("【订单使用授权优惠发送消息成功】：订单id[{}]", orderId);
                }
            }
            return ResponseUtil.success();
        } finally {
            //解锁卡券
            cardBiz.manualUnLock(model.getPatientId(), RedisConstants.LOCK_CHOICE_CARD);
            log.info("【授权折扣-卡券选择优惠解锁成功】");
        }
    }

    /**
     * 查询订单优惠明细
     *
     * @param orderId 订单id
     * @return list
     */
    public PatientOrderBenefitVo getOrderBenefit(Integer orderId) {
        PatientOrderBenefitVo result = new PatientOrderBenefitVo();
        //查询订单优惠汇总信息
        OrderBenefit summary = getOrderBenefitSummary(orderId, null);
        if (summary == null) {
            return result;
        }
        Integer benefitType = summary.getBenefitType();
        if (CARD_BENEFIT.equals(benefitType)) {
            findSetCardBenefit(summary, result);
        }
        if (AUTH_BENEFIT.equals(benefitType)) {
            findSetAuthBenefit(summary, result);
        }
        if (MIX_MATCH_BENEFIT.equals(benefitType)) {
            findSetMixMatchBenefit(summary, result);
        }
        return result;
    }

    /**
     * 查找并设置混搭优惠信息
     *
     * @param summary
     */
    private List<OrderBenefitDetailVo> findSetMixMatchBenefit(OrderBenefit summary, PatientOrderBenefitVo result) {
        Map<Integer, OrderBenefitDetailVo> resultMap = new LinkedHashMap<>(16);
        List<CardBenefit> cardBenefits = getOrderBenefitDetail(summary.getOrderId(), CardBenefit.class, cardBenefitMapper, null);
        if (CollectionUtils.isNotEmpty(cardBenefits)) {
            Map<Integer, List<CardBenefit>> listMap = cardBenefits.stream().collect(groupingBy(CardBenefit::getOrderDetailId));
            listMap.forEach((k, v) -> {
                BigDecimal itemBenefitAmount = v.stream().map(CardBenefit::getBenefitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (itemBenefitAmount.compareTo(BigDecimal.ZERO) == 0) {
                    return;
                }
                BigDecimal supplyWorkTotalLoad = v.stream()
                        .filter(obj -> ONE.equals(obj.getBenefitType()) && obj.getSupplyWorkload() != null)
                        .map(CardBenefit::getSupplyWorkload).reduce(BigDecimal.ZERO, BigDecimal::add);
                log.info("查询账单优惠明细，开单明细id：{}，计算补入工作量：{}", k, supplyWorkTotalLoad);
                OrderBenefitDetailVo vo = new OrderBenefitDetailVo();
                vo.setOrderDetailId(k);
                vo.setItemBenefitAmount(itemBenefitAmount);
                //按照优惠提交顺序排序
                v.sort(Comparator.comparing(CardBenefit::getSort));
                List<ItemUseBenefitVo> itemBenefits = v.stream().map(obj -> {
                    ItemUseBenefitVo benefitVo = new ItemUseBenefitVo();
                    benefitVo.setBenefitId(obj.getCardId());
                    benefitVo.setBenefitType(obj.getBenefitType());
                    benefitVo.setCouponType(obj.getCouponType());
                    if (MEMBER_TYPE.equals(obj.getBenefitType())) {
                        MemberType memberType = systemServiceFeign.findMemberTypeById(obj.getCardId());
                        benefitVo.setBenefitName(memberType == null ? null : memberType.getName());
                    }
                    if (COUPON_TYPE.equals(obj.getBenefitType())) {
                        CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(obj.getCouponId());
                        benefitVo.setBenefitName(coupon == null ? null : coupon.getName());
                    }
                    benefitVo.setBenefitAmount(obj.getBenefitAmount());
                    return benefitVo;
                }).collect(toList());
                vo.setItemBenefitList(itemBenefits);
                vo.setSupplyWorkload(supplyWorkTotalLoad);
                resultMap.put(k, vo);
            });
        }

        List<AuthDiscountBenefit> authBenefit = getOrderBenefitDetail(summary.getOrderId(), AuthDiscountBenefit.class, authDiscountBenefitMapper, null);
        if (CollectionUtils.isNotEmpty(authBenefit)) {
            Map<Integer, List<AuthDiscountBenefit>> listMap = authBenefit.stream()/*.filter(vo->vo.getItemType().equals(1))*/.collect(groupingBy(AuthDiscountBenefit::getOrderDetailId));
            listMap.forEach((k, v) -> {
                BigDecimal itemBenefitAmount = v.stream().map(AuthDiscountBenefit::getBenefitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (itemBenefitAmount.compareTo(BigDecimal.ZERO) > 0) {
                    OrderBenefitDetailVo vo = resultMap.computeIfAbsent(k, o->new OrderBenefitDetailVo());
                    vo.setOrderDetailId(k);
                    vo.setItemBenefitAmount(vo.getItemBenefitAmount().add(itemBenefitAmount));
                    List<ItemUseBenefitVo> itemBenefits = v.stream().map(obj -> {
                        ItemUseBenefitVo benefitVo = new ItemUseBenefitVo();
                        benefitVo.setBenefitType(AUTH_BENEFIT_TYPE);
                        benefitVo.setBenefitAmount(obj.getBenefitAmount());
                        benefitVo.setBenefitId(summary.getAuthorizedId());
                        SysUserInfoDetail author = summary.getAuthorizedId() == null ? null :
                                systemServiceFeign.findSysUserEmployeeInfoByUserId(summary.getAuthorizedId());
                        benefitVo.setBenefitName(author == null ? null : author.getName());
                        return benefitVo;
                    }).collect(toList());
                    vo.getItemBenefitList().addAll(itemBenefits);
                }
            });
        }
        return new ArrayList<>(resultMap.values());
    }

    /**
     * 查找并设置授权折扣优惠信息
     *
     * @param summary
     * @param result
     */
    private void findSetAuthBenefit(OrderBenefit summary, PatientOrderBenefitVo result) {
        List<PatientItemBenefitVo> itemList = Lists.newArrayList();
        List<AuthDiscountBenefit> authBenefit = getOrderBenefitDetail(summary.getOrderId(), AuthDiscountBenefit.class, authDiscountBenefitMapper, null);
        if (CollectionUtils.isNotEmpty(authBenefit)) {
            Map<Integer, List<AuthDiscountBenefit>> listMap = authBenefit.stream().collect(groupingBy(AuthDiscountBenefit::getOrderDetailId));
            listMap.forEach((k, v) -> {
                PatientItemBenefitVo vo = new PatientItemBenefitVo();
                BigDecimal itemBenefitAmount = v.stream().map(AuthDiscountBenefit::getBenefitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                vo.setOrderDetailId(k);
                vo.setItemBenefitAmount(itemBenefitAmount);
                List<ItemUseBenefitVo> itemBenefits = v.stream().map(obj -> {
                    ItemUseBenefitVo benefitVo = new ItemUseBenefitVo();
                    benefitVo.setBenefitType(AUTH_BENEFIT_TYPE);
                    benefitVo.setBenefitAmount(obj.getBenefitAmount());
                    benefitVo.setBenefitId(summary.getAuthorizedId());
                    SysUserInfoDetail author = summary.getAuthorizedId() == null ? null :
                            systemServiceFeign.findSysUserEmployeeInfoByUserId(summary.getAuthorizedId());
                    benefitVo.setBenefitName(author == null ? null : author.getName());
                    return benefitVo;
                }).collect(toList());
                vo.setItemBenefitList(itemBenefits);
                itemList.add(vo);
            });
        }
        result.setItemList(itemList);
    }

    /**
     * 查找并设置使用优惠信息（会员卡+卡券）
     * @param summary
     * @param orderBenefit
     */
    private void findSetCardBenefit(OrderBenefit summary, PatientOrderBenefitVo orderBenefit) {
        List<PatientItemBenefitVo> resultList = Lists.newArrayList();
        List<DeductionItemBenefitVo> deductionList = Lists.newArrayList();
        List<CardBenefit> cardBenefits = getOrderBenefitDetail(summary.getOrderId(), CardBenefit.class, cardBenefitMapper, null);
        if (CollectionUtils.isNotEmpty(cardBenefits)) {
            Map<Integer, List<CardBenefit>> listMap = cardBenefits.stream()
                    .filter(t -> !DEDUCTION.equals(t.getCouponType())).collect(groupingBy(CardBenefit::getOrderDetailId));
            Map<String, List<CardBenefit>> listMap1 = cardBenefits.stream()
                    .filter(t -> DEDUCTION.equals(t.getCouponType()))
                    .collect(groupingBy( t -> Joiner.on("-").join(t.getOrderDetailId(), t.getCouponId())));
            listMap.forEach((k, v) -> {
                PatientItemBenefitVo vo = new PatientItemBenefitVo();
                BigDecimal itemBenefitAmount = v.stream().map(CardBenefit::getBenefitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal supplyWorkTotalLoad = v.stream()
                        .filter(obj -> ONE.equals(obj.getBenefitType()) && obj.getSupplyWorkload() != null)
                        .map(CardBenefit::getSupplyWorkload).reduce(BigDecimal.ZERO, BigDecimal::add);
                log.info("查询账单优惠明细，开单明细id：{}，计算补入工作量：{}", k, supplyWorkTotalLoad);
                vo.setOrderDetailId(k);
                vo.setItemBenefitAmount(itemBenefitAmount);
                CardBenefit cardBenefit = v.get(0);
                vo.setItemId(cardBenefit.getItemId());
                vo.setType(cardBenefit.getItemType());
                //按照优惠提交顺序排序
                v.sort(Comparator.comparing(CardBenefit::getSort));
                List<ItemUseBenefitVo> itemBenefits = v.stream().map(obj -> {
                    ItemUseBenefitVo benefitVo = new ItemUseBenefitVo();
                    benefitVo.setBenefitId(obj.getCardId());
                    benefitVo.setBenefitType(obj.getBenefitType());
                    benefitVo.setCouponType(obj.getCouponType());
                    if (MEMBER_TYPE.equals(obj.getBenefitType())) {
                        MemberType memberType = systemServiceFeign.findMemberTypeById(obj.getCardId());
                        benefitVo.setBenefitName(memberType == null ? null : memberType.getName());
                    }
                    if (COUPON_TYPE.equals(obj.getBenefitType())) {
                        CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(obj.getCouponId());
                        benefitVo.setBenefitName(coupon == null ? null : coupon.getName());
                    }
                    benefitVo.setBenefitAmount(obj.getBenefitAmount());
                    return benefitVo;
                }).collect(toList());
                vo.setItemBenefitList(itemBenefits);
                vo.setSupplyWorkload(supplyWorkTotalLoad);
                resultList.add(vo);
            });

            listMap1.forEach((k, v) -> {
                DeductionItemBenefitVo vo = new DeductionItemBenefitVo();
                BigDecimal itemBenefitAmount = v.stream().map(CardBenefit::getBenefitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal supplyWorkTotalLoad = v.stream()
                        .filter(obj -> ONE.equals(obj.getBenefitType()) && obj.getSupplyWorkload() != null)
                        .map(CardBenefit::getSupplyWorkload).reduce(BigDecimal.ZERO, BigDecimal::add);
                Integer orderDetailId = Integer.valueOf(k.split("-")[0]);
                log.info("查询账单划扣优惠明细，开单明细id：{}，计算补入工作量：{}", orderDetailId, supplyWorkTotalLoad);
                vo.setOrderDetailId(orderDetailId);
                vo.setQuantity(v.size());
                vo.setItemBenefitAmount(itemBenefitAmount);
                CardBenefit cardBenefit = v.get(0);
                vo.setItemId(cardBenefit.getItemId());
                vo.setType(cardBenefit.getItemType());
                BigDecimal totalDeduct = v.stream().map(t -> {
                    List<DeductionItemPeriod> list = periodBiz.list(t.getCardId());
                    return list.stream().filter(t1 -> Objects.equals(t1.getItemId(),vo.getItemId()) && Objects.equals(t1.getType(),vo.getType()))
                            .map(DeductionItemPeriod::getPackageUnitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
                vo.setDeductionAmount(totalDeduct);
                //按照优惠提交顺序排序
                v.sort(Comparator.comparing(CardBenefit::getSort));
                List<ItemUseBenefitVo> itemBenefits = v.stream().map(obj -> {
                    ItemUseBenefitVo benefitVo = new ItemUseBenefitVo();
                    benefitVo.setBenefitId(obj.getCardId());
                    benefitVo.setBenefitType(obj.getBenefitType());
                    benefitVo.setCouponType(obj.getCouponType());
                    if (MEMBER_TYPE.equals(obj.getBenefitType())) {
                        MemberType memberType = systemServiceFeign.findMemberTypeById(obj.getCardId());
                        benefitVo.setBenefitName(memberType == null ? null : memberType.getName());
                    }
                    if (COUPON_TYPE.equals(obj.getBenefitType())) {
                        CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(obj.getCouponId());
                        benefitVo.setBenefitName(coupon == null ? null : coupon.getName());
                    }
                    benefitVo.setBenefitAmount(obj.getBenefitAmount());
                    return benefitVo;
                }).collect(toList());
                vo.setItemBenefitList(itemBenefits);
                vo.setSupplyWorkload(supplyWorkTotalLoad);
                deductionList.add(vo);
            });
        }
        orderBenefit.setItemList(resultList);
        orderBenefit.setDeductionList(deductionList);
    }

    @Transactional
    public RestErrorBo revokeBenefit(Integer orderId) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        //查询订单优惠汇总信息
        OrderBenefit summary = getOrderBenefitSummary(orderId, ZERO);
        if (summary == null) {
            errorBo.setError(DiscountError.ORDER_NO_BENEFIT);
            return errorBo;
        }
        //更新优惠券
        if (CARD_BENEFIT.equals(summary.getBenefitType())) {
            revokeCardBenefit(orderId, loginUserId);
        }
        //更新授权
        if (AUTH_BENEFIT.equals(summary.getBenefitType())) {
            revokeAuthBenefit(orderId, loginUserId);
        }
        // 更新混搭优惠
        if (MIX_MATCH_BENEFIT.equals(summary.getBenefitType())) {
            revokeCardBenefit(orderId, loginUserId);
            revokeAuthBenefit(orderId, loginUserId);
        }
        //更新订单优惠总信息
        OrderBenefit orderBenefit = new OrderBenefit();
        orderBenefit.setDeleted(TRUE.getCode());
        orderBenefit.setUpdId(loginUserId);
        updateOrderBenefit(orderBenefit, orderId, OrderBenefit.class, orderBenefitMapper);
        mqServiceFeign.sendMessage(orderId, DELETE, BaseBenefit);
        log.info("【订单撤销优惠发送消息成功】：订单id[{}]", orderId);
        return errorBo;
    }

    /**
     * 撤销授权折扣
     *
     * @param orderId
     * @param loginUserId
     */
    private void revokeAuthBenefit(Integer orderId, Integer loginUserId) {
        AuthDiscountBenefit authDiscountBenefit = new AuthDiscountBenefit();
        authDiscountBenefit.setDeleted(TRUE.getCode());
        authDiscountBenefit.setOperateType(MODIFY_BILL.getCode());
        authDiscountBenefit.setUpdId(loginUserId);
        updateOrderBenefit(authDiscountBenefit, orderId, AuthDiscountBenefit.class, authDiscountBenefitMapper);
    }

    /**
     * 撤销卡券优惠
     *
     * @param orderId
     * @param loginUserId
     */
    private void revokeCardBenefit(Integer orderId, Integer loginUserId) {
        List<CardBenefit> revokeCards = getOrderBenefitDetail(orderId, CardBenefit.class, cardBenefitMapper, ZERO);
        CardBenefit cardBenefit = new CardBenefit();
        cardBenefit.setDeleted(TRUE.getCode());
        cardBenefit.setOperateType(MODIFY_BILL.getCode());
        cardBenefit.setUpdId(loginUserId);
        updateOrderBenefit(cardBenefit, orderId, CardBenefit.class, cardBenefitMapper);
        //更新卡券状态
        Set<Card> updateCards = updateRevokeCardStatus(revokeCards);
        updateCards.forEach(obj -> mqServiceFeign.sendMessage(obj.getId(), UPDATE, BaseCardSingle));
        log.info("【订单撤销卡券优惠，更新卡券发送消息成功】：卡券ids：{}", updateCards.stream().map(Card::getId).collect(toList()));
    }

    /**
     * 查询账单的使用优惠券信息
     *
     * @param orderId orderId
     * @return String
     */
    public String getOrderCoupon(Integer orderId) {
        List<BillUsedCouponBo> billCoupons = cardBenefitMapper.getBillCoupons(orderId);
        List<String> cardNames = billCoupons.stream().map(obj -> {
            if (MEMBER_TYPE.equals(obj.getBenefitType())) {
                MemberType memberType = systemServiceFeign.findMemberTypeById(obj.getCouponId());
                return memberType != null ? memberType.getName() : null;
            }
            return obj.getCouponName();
        }).collect(toList());
        if (CollectionUtils.isNotEmpty(cardNames)) {
            return Joiner.on(",").join(cardNames);
        }
        return null;
    }

    /**
     * 对象转换
     *
     * @param model model
     * @return PatientChooseBenefitForm
     */
    private PatientChooseBenefitForm benefitTransformToForm(PatientOrderBenefitModel model) {
        return BeanCopierUtils.generalCopyBean(model, PatientChooseBenefitForm.class);
    }

    /**
     * 计算工作量
     * @param itemUseBenefitBo OrderItemUseBo
     * @param itemBenefitBo itemBenefitBo
     */
    private BigDecimal calculateWordLoad(ItemUseBenefitBo itemUseBenefitBo, OrderItemUseBo itemBenefitBo) {
        Example example;
        BigDecimal supplyWorkload = BigDecimal.ZERO;
        if (COUPON_TYPE.equals(itemUseBenefitBo.getBenefitType())) {
            Integer couponType = itemUseBenefitBo.getCouponType();
            Integer couponId = itemUseBenefitBo.getCouponId();
            Integer cardId = itemUseBenefitBo.getBenefitId();
            if (VOUCHER.equals(couponType)) {
                example = new Example(VoucheCoupon.class);
                example.createCriteria().andEqualTo("couponId", couponId);
                VoucheCoupon voucheCoupon = voucherMapper.selectOneByExample(example);
                if (voucheCoupon != null) {
                    supplyWorkload = supplyWorkload.add(itemUseBenefitBo.getBenefitAmount()
                            .multiply(voucheCoupon.getWorkloadRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            }
            if (DISCOUNT.equals(couponType)) {
                example = new Example(DiscountCoupon.class);
                example.createCriteria().andEqualTo("couponId", couponId);
                DiscountCoupon discountCoupon = discountCouponMapper.selectOneByExample(example);
                if (discountCoupon != null) {
                    supplyWorkload = supplyWorkload.add(itemUseBenefitBo.getBenefitAmount()
                            .multiply(discountCoupon.getWorkloadRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            }
            if (EXCHANGE.equals(couponType)) {
                example = new Example(PackageCouponItem.class);
                example.createCriteria().andEqualTo("couponId", couponId).andEqualTo("itemId", itemBenefitBo.getItemId()).
                        andEqualTo("type", itemBenefitBo.getType());
                PackageCouponItem packageCouponItem = packageCouponItemMapper.selectOneByExample(example);
                if (packageCouponItem != null) {
                    supplyWorkload = supplyWorkload.add(packageCouponItem.getWorkloadLoad());
                }
            }
            if (SPECIAL_PACKAGE.equals(couponType)) {
                example = new Example(SpecialPackageCouponItem.class);
                example.createCriteria().andEqualTo("couponId", couponId).andEqualTo("itemId", itemBenefitBo.getItemId()).
                        andEqualTo("type", itemBenefitBo.getType());
                SpecialPackageCouponItem specialPackageCouponItem = specialPackageCouponItemMapper.selectOneByExample(example);
                if (specialPackageCouponItem != null) {
                    supplyWorkload = supplyWorkload.add(specialPackageCouponItem.getWorkloadLoad());
                }
            }
            if (DEDUCTION.equals(couponType)) {
                List<DeductionItemPeriod> list = periodBiz.list(cardId);
                DeductionItemPeriod period = list.stream().filter(t -> itemBenefitBo.getItemId().equals(t.getItemId())).findFirst().orElse(null);
                if (period != null) {
                    supplyWorkload = supplyWorkload.add(period.getWorkloadLoad());
                }
            }
        }

        log.info("保存优惠，开单明细id：{}，计算补入工作量：{}", itemBenefitBo.getOrderDetailId(), supplyWorkload);
        return supplyWorkload;
    }

    private RestErrorBo checkAuthItem(List<OrderDetailChargeVO> orderDetails, List<AuthItemBenefitModel> itemBenefits) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        Map<Integer, OrderDetailChargeVO> orderDetailMap = orderDetails.stream().collect(toMap(OrderDetailChargeVO::getOrderDetailId, Function.identity(), (v1, v2) -> v2));
        for (AuthItemBenefitModel item : itemBenefits) {
            OrderDetailChargeVO detail = orderDetailMap.get(item.getOrderDetailId());
            if (detail == null) {
                errorBo.setError(DiscountError.ORDER_ITEM_NOT_EXIST);
                errorBo.setMsg(item.getItemId());
                return errorBo;
            }
            if (detail.getReceivableAmount().compareTo(item.getBenefitAmount()) < 0) {
                errorBo.setError(DiscountError.AUTH_BENEFIT_AMOUNT_ERROR);
                errorBo.setMsg(item.getItemId());
                return errorBo;
            }
        }
        return errorBo;
    }

    /**
     * 查询订单总优惠信息
     *
     * @param orderId orderId
     * @return order
     */
    private OrderBenefit getOrderBenefitSummary(Integer orderId, Integer deleteStatus) {
        Example example = new Example(OrderBenefit.class);
        if (deleteStatus != null) {
            example.createCriteria().andEqualTo("deleted", deleteStatus)
                    .andEqualTo("orderId", orderId);
        } else {
            example.createCriteria().andEqualTo("orderId", orderId);
        }
        return orderBenefitMapper.selectOneByExample(example);
    }

    /**
     * 获取订单优惠明细（优惠券或授权折扣）
     *
     * @param orderId orderId
     * @param clazz   clazz
     * @param mapper  mapper
     */
    private <T> List<T> getOrderBenefitDetail(Integer orderId, Class<?> clazz, Mapper<T> mapper, Integer deleteStatus) {
        Example example = new Example(clazz);
        if (deleteStatus != null) {
            example.createCriteria().andEqualTo("deleted", deleteStatus)
                    .andEqualTo("orderId", orderId);
        } else {
            example.createCriteria().andEqualTo("orderId", orderId);
        }
        return mapper.selectByExample(example);
    }

    private <T> void updateOrderBenefit(T t, Integer orderId, Class<?> clazz, Mapper<T> mapper) {
        Example example = new Example(clazz);
        example.createCriteria().andEqualTo("orderId", orderId)
                .andEqualTo("deleted", FALSE.getCode());
        mapper.updateByExampleSelective(t, example);
    }

    /**
     * 获取卡券使用情况
     *
     * @param list list
     * @return map
     */
    private Map<Integer, CardUseBo> getCardUseList(List<CardBenefit> list) {
        Set<Integer> exchangeCards = list.stream()
                .filter(obj -> EXCHANGE.equals(obj.getCouponType()))
                .map(CardBenefit::getCardId).collect(toSet());
        Set<Integer> packageCards = list.stream()
                .filter(obj -> SPECIAL_PACKAGE.equals(obj.getCouponType()))
                .map(CardBenefit::getCardId).collect(toSet());
        Set<Integer> deductionCards = list.stream()
                .filter(obj -> DEDUCTION.equals(obj.getCouponType()))
                .map(CardBenefit::getCardId).collect(toSet());
        Map<Integer, CardUseBo> map = Maps.newHashMapWithExpectedSize(list.size());
        if (CollectionUtils.isNotEmpty(exchangeCards)) {
            List<CardUseBo> useList = cardBenefitMapper.getCardUseInfo(exchangeCards, EXCHANGE.getCode());
            Map<Integer, CardUseBo> useBoMap = useList.stream().collect(toMap(CardUseBo::getCardId, Function.identity()));
            if (!useBoMap.isEmpty()) {
                map.putAll(useBoMap);
            }
        }
        if (CollectionUtils.isNotEmpty(packageCards)) {
            List<CardUseBo> useList = cardBenefitMapper.getCardUseInfo(packageCards, SPECIAL_PACKAGE.getCode());
            Map<Integer, CardUseBo> useBoMap = useList.stream().collect(toMap(CardUseBo::getCardId, Function.identity()));
            if (!useBoMap.isEmpty()) {
                map.putAll(useBoMap);
            }
        }
        if (CollectionUtils.isNotEmpty(deductionCards)) {
            List<CardUseBo> useList = deductionCards.stream().map(t -> cardBenefitMapper.getCardUseInfo(Sets.newHashSet(t), DEDUCTION.getCode()).get(0))
                    .collect(toList());
            Map<Integer, CardUseBo> useBoMap = useList.stream().collect(toMap(CardUseBo::getCardId, Function.identity()));
            if (!useBoMap.isEmpty()) {
                map.putAll(useBoMap);
            }
        }
        return map;
    }

    /**
     * 返回设置卡券更新信息
     *
     * @param list list
     * @return list
     */
    private Set<Card> getUpdateCards(List<CardBenefit> list) {
        //查询兑换券，套餐券的使用信息
        Map<Integer, CardUseBo> useBoMap = getCardUseList(list);
        //更新卡券状态
        return list.stream().filter(obj -> COUPON_TYPE.equals(obj.getBenefitType())).map(obj -> {
            Card card = new Card();
            CardUseBo cardUseBo = useBoMap.get(obj.getCardId());
            card.setId(obj.getCardId());
            if (VOUCHER.equals(obj.getCouponType()) || DISCOUNT.equals(obj.getCouponType())) {
                card.setStatus(USE_ALL.getCode());
            }
            if (EXCHANGE.equals(obj.getCouponType()) || SPECIAL_PACKAGE.equals(obj.getCouponType())
                    || DEDUCTION.equals(obj.getCouponType())) {
                card.setStatus(ONE_TIME_USE.equals(cardUseBo.getUseWay()) ?
                        USE_ALL.getCode() : ZERO.equals(cardUseBo.getUsable()) ?
                        USE_ALL.getCode() : PARTIAL_USE.getCode());
            }
            return card;
        }).collect(toSet());
    }

    /**
     * 获取撤销优惠的卡券信息
     *
     * @param list list
     * @return list
     */
    private Set<Card> getRevokeUpdateCards(List<CardBenefit> list) {
        //查询兑换券，套餐券的使用信息
        Map<Integer, CardUseBo> useBoMap = getCardUseList(list);
        //更新卡券状态
        return list.stream().filter(obj -> COUPON_TYPE.equals(obj.getBenefitType())).map(obj -> {
            Card card = new Card();
            CardUseBo cardUseBo = useBoMap.get(obj.getCardId());
            card.setId(obj.getCardId());
            if (VOUCHER.equals(obj.getCouponType()) || DISCOUNT.equals(obj.getCouponType())) {
                card.setStatus(ACTIVATED.getCode());
            }
            if (EXCHANGE.equals(obj.getCouponType()) || SPECIAL_PACKAGE.equals(obj.getCouponType())) {
                if (ONE_TIME_USE.equals(cardUseBo.getUseWay())) {
                    card.setStatus(ACTIVATED.getCode());
                } else {
                    if (cardUseBo.getUseCount() == 0) {
                        card.setStatus(ACTIVATED.getCode());
                    }
                    if (ONE.equals(cardUseBo.getUsable()) && cardUseBo.getUseCount() > 0) {
                        card.setStatus(PARTIAL_USE.getCode());
                    }
                }
            }
            return card;
        }).collect(toSet());
    }

    /**
     * 使用优惠更新卡券状态
     *
     * @param cardBenefits 优惠卡券集合
     */
    private Set<Card> updateCardStatus(List<CardBenefit> cardBenefits) {
        Set<Card> updateCards = getUpdateCards(cardBenefits);
        if (CollectionUtils.isNotEmpty(updateCards)) {
            //更新卡券状态
            updateCards.forEach(obj -> cardMapper.updateByPrimaryKeySelective(obj));
        }
        return updateCards;
    }

    /**
     * 撤销优惠更新卡券状态
     *
     * @param cardBenefits 优惠卡券集合
     */
    private Set<Card> updateRevokeCardStatus(List<CardBenefit> cardBenefits) {
        Set<Card> updateCards = getRevokeUpdateCards(cardBenefits);
        if (CollectionUtils.isNotEmpty(updateCards)) {
            //更新卡券状态
            updateCards.forEach(obj -> cardMapper.updateByPrimaryKeySelective(obj));
        }
        return updateCards;
    }

    /**
     * 查询订单使用优惠数量
     *
     * @param orderId orderId
     * @return int
     */
    private int countOrder(Integer orderId) {
        Example example = new Example(OrderBenefit.class);
        example.createCriteria().andEqualTo("orderId", orderId)
                .andEqualTo("deleted", ZERO);
        return orderBenefitMapper.selectCountByExample(example);
    }

    /**
     * 查询门诊项目分类的优惠金额合计和补入工作量合计
     *
     * @param queryForm
     * @return
     */
    public List<ClinicTariffDiscountCouponVO> findClinicTariffCategoryDiscountCoupon(DiscountCouponQuery queryForm) {
        return cardBenefitMapper.selectClinicTariffCategoryDiscountCoupon(queryForm);
    }

    /**
     * 收费 - 混搭优惠
     *
     * @param model model
     * @return ResponseResult
     */
    @Transactional
    public ResponseResult saveMixMatchBenefit(MixMatchBenefitModel model) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer orderId = model.getOrderId();
        BigDecimal totalAmount = BigDecimal.ZERO;
        Boolean isUsedBenefit = false;

        //校验是否使用过优惠
        int count = countOrder(orderId);
        if (count != 0) {
            return ResponseUtil.error(DiscountError.ORDER_HAS_BENEFIT);
        }

        //校验是否使用过优惠
        SysEmployee employee = systemServiceFeign.findSysEmployeeById(model.getAuthorizedId());
        if (employee == null || !employee.getDiscount()) {
            log.warn("【授权折扣优惠】授权人没有权限进行授权折扣");
            return ResponseUtil.error(DiscountError.EMPLOYEE_NO_AUTH_DISCOUNT);
        }
        //获取订单明细
        List<OrderDetailChargeVO> orderDetails = treatmentServiceFeign.findOrderDetailByOrderRecordId(orderId);
        if (CollectionUtils.isEmpty(orderDetails)) {
            return ResponseUtil.error(DiscountError.ORDER_NOT_EXIST);
        }
        List<AuthItemBenefitModel> itemBenefits = model.getItemBenefits();
        if (StringHelper.isNotEmpty(itemBenefits)) {
            RestErrorBo errorBo = checkAuthItem(orderDetails, itemBenefits);
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
            }
        }

        log.info("收费时选择的优惠券信息：[{}]", model);
        //设置门诊为开单门诊（重置组织id）
        this.setOrderField(model);
        PatientChooseBenefitForm benefitForm = benefitTransformToForm(model);
        //查询订单项目对应的优惠
        ResponseResult result = cardBiz.choiceBenefitBo(benefitForm);
        log.info("收费时选择的优惠券信息匹配优惠：{}", result);
        if (!FALSE.getCode().equals(result.getStatus())) {
            return result;
        }
        // 卡券优惠
        try {
            List<OrderItemUseBo> data = (List<OrderItemUseBo>) result.getData();
            List<CardBenefit> list = Lists.newArrayList();
            CardBenefit cardBenefit;
            for (OrderItemUseBo itemBenefitBo : data) {
                List<ItemUseBenefitBo> itemUseBenefitBos = itemBenefitBo.getItemUseBenefitBos();
                if (CollectionUtils.isNotEmpty(itemUseBenefitBos)) {
                    for (ItemUseBenefitBo itemUseBenefitBo : itemUseBenefitBos) {
                        cardBenefit = new CardBenefit();
                        cardBenefit.setOrgId(benefitForm.getOrgId());
                        cardBenefit.setOrderId(benefitForm.getOrderId());
                        cardBenefit.setOrderDetailId(itemBenefitBo.getOrderDetailId());
                        cardBenefit.setCouponId(itemUseBenefitBo.getCouponId());
                        cardBenefit.setPatientId(benefitForm.getPatientId());
                        cardBenefit.setCardId(itemUseBenefitBo.getBenefitId());
                        cardBenefit.setItemId(itemBenefitBo.getItemId());
                        cardBenefit.setItemType(itemBenefitBo.getType());
                        cardBenefit.setCouponType(itemUseBenefitBo.getCouponType());
                        cardBenefit.setBenefitType(itemUseBenefitBo.getBenefitType());
                        cardBenefit.setItemIndex(itemUseBenefitBo.getItemIndex());
                        cardBenefit.setBenefitAmount(itemUseBenefitBo.getBenefitAmount());
                        cardBenefit.setOperateType(CHARGE.getCode());
                        cardBenefit.setCrtId(loginUserId);
                        cardBenefit.setUpdId(loginUserId);
                        cardBenefit.setSort(itemUseBenefitBo.getId());
                        //计算工作量
                        cardBenefit.setSupplyWorkload(calculateWordLoad(itemUseBenefitBo,itemBenefitBo));
                        list.add(cardBenefit);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                isUsedBenefit = true;
                totalAmount = data.stream()
                        .filter(obj -> obj.getBenefitAmount() != null).map(OrderItemUseBo::getBenefitAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                cardBenefitMapper.insertList(list);
                //需要更新的卡券
                Set<Card> updateCards = updateCardStatus(list);
                log.info("【订单使用卡券优惠发送消息成功】：订单id[{}]", orderId);
                updateCards.forEach(obj -> mqServiceFeign.sendMessage(obj.getId(), UPDATE, BaseCardSingle));
                log.info("【订单使用卡券优惠，更新卡券发送消息成功】：卡券ids：{}", updateCards.stream().map(Card::getId).collect(toList()));
            }
        } finally {
            //解锁卡券
            cardBiz.manualUnLock(loginUserId, RedisConstants.LOCK_CHOICE_CARD);
            log.info("【保存卡券优惠解锁成功】");
        }

        // 授权折扣
        try {
            AuthDiscountBenefit authDiscountBenefit;
            List<AuthDiscountBenefit> list = Lists.newArrayList();
            if (StringHelper.isNotEmpty(itemBenefits)) {
                for (AuthItemBenefitModel itemBenefit : itemBenefits) {
                    authDiscountBenefit = new AuthDiscountBenefit();
                    authDiscountBenefit.setOrgId(model.getOrgId());
                    authDiscountBenefit.setOrderId(orderId);
                    authDiscountBenefit.setOrderDetailId(itemBenefit.getOrderDetailId());
                    authDiscountBenefit.setPatientId(model.getPatientId());
                    authDiscountBenefit.setItemId(itemBenefit.getItemId());
                    authDiscountBenefit.setItemType(itemBenefit.getType());
                    authDiscountBenefit.setBenefitAmount(itemBenefit.getBenefitAmount());
                    authDiscountBenefit.setOperateType(CHARGE.getCode());
                    authDiscountBenefit.setCrtId(loginUserId);
                    authDiscountBenefit.setUpdId(loginUserId);
                    list.add(authDiscountBenefit);
                }
                if (CollectionUtils.isNotEmpty(list)) {
                    isUsedBenefit = true;
                    BigDecimal totalBenefitAmount = itemBenefits.stream().map(AuthItemBenefitModel::getBenefitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    totalAmount = totalAmount.add(totalBenefitAmount);
                    authDiscountBenefitMapper.insertList(list);
                    log.info("【订单使用授权优惠发送消息成功】：订单id[{}]", orderId);
                }
            }
            if (isUsedBenefit) {
                insertOrderBenefit(orderId, totalAmount, loginUserId, model.getAuthorizedId(), model.getRemark(), MIX_MATCH_BENEFIT);
                mqServiceFeign.sendMessage(orderId, ADD, BaseBenefit);
            }
            return ResponseUtil.success();
        } finally {
            //解锁卡券
            cardBiz.manualUnLock(model.getPatientId(), RedisConstants.LOCK_CHOICE_CARD);
            log.info("【授权折扣-卡券选择优惠解锁成功】");
        }
    }

    /**
     * 保存订单优惠
     *
     * @param orderId
     * @param totalAmount
     * @param loginUserId
     * @param authorizedId
     * @param remark
     * @param benefitTypeEnum
     */
    public void insertOrderBenefit(Integer orderId, BigDecimal totalAmount, Integer loginUserId, Integer authorizedId, String remark, ChoiceBenefitTypeEnum benefitTypeEnum) {
        OrderBenefit orderBenefit = new OrderBenefit();
        orderBenefit.setOrderId(orderId);
        orderBenefit.setTotalAmount(totalAmount);
        orderBenefit.setBenefitType(benefitTypeEnum.getCode());
        orderBenefit.setAuthorizedId(authorizedId);
        orderBenefit.setRemark(remark);
        orderBenefit.setCrtId(loginUserId);
        orderBenefit.setUpdId(loginUserId);
        orderBenefitMapper.insertSelective(orderBenefit);
    }
}
