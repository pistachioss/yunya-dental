package com.yunya.modules.discount.biz;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.domain.form.CouponOrderForm;
import com.yunya.feign.discount.domain.model.CouponOrderDetailModel;
import com.yunya.feign.discount.domain.model.CouponOrderModel;
import com.yunya.feign.discount.domain.vo.CouponOrderDetailVO;
import com.yunya.feign.discount.domain.vo.CouponOrderVO;
import com.yunya.feign.discount.domain.vo.CouponPayDetailVO;
import com.yunya.feign.middle.RemoteMiddleServiceFeign;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.models.discount.*;
import com.yunya.modules.discount.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseCardSingle;
import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseCouponBill;
import static com.yunya.framework.common.constant.BusinessConstants.UPDATE;
import static com.yunya.modules.discount.enums.CouponOrderError.COUPON_STOCK_LACK;
import static com.yunya.modules.discount.enums.CouponOrderError.SALE_CHANNEL_NULL;
import static java.util.stream.Collectors.*;

/**
 * @auther: xy
 * @date: 2023/6/26
 */
@Service
@Slf4j
public class CouponOrderBiz {
    @Resource
    private CardMapper cardMapper;
    @Resource
    private CardBenefitMapper cardBenefitMapper;
    @Resource
    private CouponCommonInfoMapper couponMapper;
    @Resource
    private CouponOrderMapper couponOrderMapper;
    @Resource
    private CouponOrderDetailMapper orderDetailMapper;
    @Resource
    private CouponChangeRecordMapper changeRecordMapper;
    @Resource
    private CouponOrderVirtualMapper virtualMapper;
    @Resource
    private CouponBillPayMapper billPayMapper;
    @Resource
    private DeductionPeriodBiz periodBiz;
    @Resource
    private SalesChannelBiz salesChannelBiz;
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private RemotePatientCentralServiceFeign patientFeign;
    @Autowired
    private RemoteRabbitMqServiceFeign mqServiceFeign;
    @Resource
    private RemoteMiddleServiceFeign middleServiceFeign;

    @Transactional(rollbackFor = Exception.class)
    public CouponOrderVO soldCard(CouponOrderModel model) {
        log.info("划扣下单参数：{}", model);
        int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        List<CouponOrderDetailModel> detail = model.getDetail();
        List<Integer> couponIds = detail.stream().map(CouponOrderDetailModel::getCouponId).collect(toList());
        Map<Integer, CouponCommonInfo> collect = listCoupon(couponIds);
        List<Card> list = null;
        CouponOrderVO vo = null;
        try {
            List<Card> cards = cardMapper.listRemaining(couponIds, orgId);
            checkRemaining(cards, detail, collect);
            LocalDateTime now = LocalDateTime.now();
            list = selectCard(model.getPatientId(), cards, detail, now);
            CouponOrder couponOrder = new CouponOrder();
            List<DeductionItemPeriod> periods = periodBiz.listByCoupon(couponIds, DateUtil.localDateTimeToDate(now));
            Map<Integer, List<DeductionItemPeriod>> periodsCollect = periods.stream().collect(groupingBy(DeductionItemPeriod::getCouponId, toList()));
            List<CouponOrderDetail> build = build(model.getPatientId(), detail, collect, couponOrder, now,periodsCollect);
            couponOrderMapper.insertSelective(couponOrder);
            build.forEach(o -> o.setOrderId(couponOrder.getId()));
            orderDetailMapper.insertList(build);
            List<CouponOrderVirtual> virtuals = orderVirtual(couponOrder, list, now, collect, periodsCollect);
            virtualMapper.insertList(virtuals);
            vo = detail(couponOrder.getId());
            mqServiceFeign.sendMessage(couponOrder.getId(), 0, BaseCouponBill);
        } catch (Exception e) {
            if (CollectionUtils.isNotEmpty(list)) {
                revoke(list);
            }
            throw e;
        }
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public CouponOrderVO edit(CouponOrderForm form) {
        log.info("划扣修改下单参数：{}", form);
        int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        List<CouponOrderDetailModel> detail = form.getDetail();
        List<Integer> couponIds = detail.stream().map(CouponOrderDetailModel::getCouponId).collect(toList());
        Map<Integer, CouponCommonInfo> collect = listCoupon(couponIds);
        List<Card> list = null;
        CouponOrderVO vo = null;
        try {
            Integer orderId = form.getOrderId();
            CouponOrder order = getOrder(orderId);
            if (Objects.nonNull(order)) {
                deleteDetail(orderId);
                deleteVirtual(orderId);
                List<Card> cards = cardMapper.listRemaining(couponIds, orgId);
                checkRemaining(cards, detail, collect);
                LocalDateTime now = LocalDateTime.now();
                list = selectCard(order.getPatientId(), cards, detail, now);
                List<DeductionItemPeriod> periods = periodBiz.listByCoupon(couponIds, DateUtil.localDateTimeToDate(now));
                Map<Integer, List<DeductionItemPeriod>> periodsCollect = periods.stream().collect(groupingBy(DeductionItemPeriod::getCouponId, toList()));
                List<CouponOrderDetail> build = build(order.getPatientId(), detail, collect, order, now,periodsCollect);
                couponOrderMapper.updateByPrimaryKeySelective(order);
                build.forEach(o -> o.setOrderId(order.getId()));
                orderDetailMapper.insertList(build);
                List<CouponOrderVirtual> virtuals = orderVirtual(order, list, now, collect,periodsCollect);
                virtualMapper.insertList(virtuals);
                vo = detail(order.getId());
                mqServiceFeign.sendMessage(order.getId(), 0, BaseCouponBill);
            }
        } catch (Exception e) {
            if (CollectionUtils.isNotEmpty(list)) {
                revoke(list);
            }
            throw e;
        }
        return vo;
    }

    public void occur(Integer orderId, Integer occurType, Integer patientId, Integer cardId, BigDecimal amount) {
        int id = Integer.parseInt(BaseContextHandler.getUserID());
        Date date = new Date();
        if (Objects.equals(occurType, 1)) {
            List<CouponOrderVirtual> cards = listOrderVirtual(orderId, null, true);
            CouponBillPay billPay = listPay(orderId);
            if (CollectionUtils.isNotEmpty(cards)) {
                List<CouponChangeRecord> list = cards.stream().map(t -> {
                    CouponChangeRecord latest = changeRecordMapper.getLatest(t.getPatientId(), t.getCardId());
                    BigDecimal curren = Objects.isNull(latest) ? BigDecimal.ZERO : latest.getCurrentAmount();
                    CouponChangeRecord couponChangeRecord = new CouponChangeRecord();
                    couponChangeRecord.setOrgId(billPay.getOrgId());
                    couponChangeRecord.setPatientId(patientId);
                    couponChangeRecord.setOrderId(orderId);
                    couponChangeRecord.setCardId(t.getCardId());
                    couponChangeRecord.setCouponId(t.getCouponId());
                    couponChangeRecord.setOccurType(occurType);
                    couponChangeRecord.setOccurAmount(t.getPackageUnitPrice());
                    couponChangeRecord.setCurrentAmount(curren.add(t.getPackageUnitPrice()));
                    couponChangeRecord.setOperatorUserId(id);
                    couponChangeRecord.setCardNumber(t.getCardNumber());
                    couponChangeRecord.setOccurDate(date);
                    return couponChangeRecord;
                }).collect(toList());
                log.info("划扣结存购买：{}", JSON.toJSONString(list));
                list.forEach(t -> {
                    changeRecordMapper.insertSelective(t);
                    middleServiceFeign.occur(t);
                });
            }
        }
        if (Objects.equals(occurType, 2)) {
            List<CardBenefit> orderBenefit = getOrderBenefit(orderId);
            Map<Integer, List<String>> collect = orderBenefit.stream().collect(groupingBy(CardBenefit::getCardId, mapping(t -> Joiner.on("-").join(t.getItemId(), t.getItemType()), toList())));
            for (Map.Entry<Integer, List<String>> entry : collect.entrySet()) {
                Integer k = entry.getKey();
                List<String> v = entry.getValue();
                CouponChangeRecord latest = changeRecordMapper.getLatest(patientId, k);
                if (Objects.isNull(latest)) {
                    continue;
                }
                List<DeductionItemPeriod> list = periodBiz.list(k);
                BigDecimal reduce = list.stream().filter(t1 -> {
                    String join = Joiner.on("-").join(t1.getItemId(), t1.getType());
                    return v.contains(join);
                }).map(DeductionItemPeriod::getPackageUnitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                CouponChangeRecord newBean = BeanCopierUtils.generalCopyBean(latest, CouponChangeRecord.class);
                newBean.setOrderId(orderId);
                newBean.setOccurType(occurType);
                newBean.setOccurAmount(reduce);
                newBean.setCurrentAmount(newBean.getCurrentAmount().subtract(newBean.getOccurAmount()));
                newBean.setOperatorUserId(id);
                newBean.setOccurDate(date);
                newBean.setId(null);
                log.info("划扣结存消费：{}", JSON.toJSONString(newBean));
                changeRecordMapper.insertSelective(newBean);
                middleServiceFeign.occur(newBean);
            }
        }
        if (Objects.equals(occurType, 3)) {
            CouponChangeRecord latest = changeRecordMapper.getLatest(patientId, cardId);
            if (Objects.isNull(latest)) {
                return;
            }
            CouponChangeRecord newBean = BeanCopierUtils.generalCopyBean(latest, CouponChangeRecord.class);
            newBean.setOrderId(orderId);
            newBean.setOccurType(occurType);
            newBean.setOccurAmount(amount);
            newBean.setCurrentAmount(newBean.getCurrentAmount().subtract(newBean.getOccurAmount()));
            newBean.setOperatorUserId(id);
            newBean.setOccurDate(date);
            newBean.setId(null);
            log.info("划扣结存退费：{}", JSON.toJSONString(newBean));
            changeRecordMapper.insertSelective(newBean);
            middleServiceFeign.occur(newBean);
        }
    }

    public List<CardBenefit> getOrderBenefit(Integer orderId) {
        Example example = new Example(CardBenefit.class);
        example.createCriteria()
                .andEqualTo("orderId", orderId)
                .andEqualTo("deleted", 0)
                .andEqualTo("couponType", 5);
        return cardBenefitMapper.selectByExample(example);
    }

    private List<CouponOrderDetail> build(Integer patientId, List<CouponOrderDetailModel> detail, Map<Integer, CouponCommonInfo> collect1
            , CouponOrder couponOrder, LocalDateTime now, Map<Integer, List<DeductionItemPeriod>> periodsCollect) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        Date date = DateUtil.localDateTimeToDate(now);
        SalesChannel name = salesChannelBiz.getByName("艾维门诊");
        if (Objects.isNull(name)) {
            throw ClientServiceException.wrap(SALE_CHANNEL_NULL);
        }
        couponOrder.setOrgId(Objects.isNull(couponOrder.getOrgId()) ? orgId : couponOrder.getOrgId());
        couponOrder.setPatientId(patientId);
        couponOrder.setOrderRecordNum(StringUtils.isBlank(couponOrder.getOrderRecordNum())
                ? generateOrderRecordNumber(orgId) : couponOrder.getOrderRecordNum());
        couponOrder.setStatus(0);
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalReceivable = BigDecimal.ZERO;
        CouponOrderDetail orderDetail;
        List<CouponOrderDetail> list = Lists.newArrayList();
        for (CouponOrderDetailModel detailModel : detail) {
            Integer couponId = detailModel.getCouponId();
            Integer quantity = detailModel.getQuantity();
            List<DeductionItemPeriod> period = periodsCollect.get(couponId);
            CouponCommonInfo coupon = collect1.get(couponId);
            BigDecimal price = period.stream().map(p -> p.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalPrice = totalPrice.add(price);
            orderDetail = new CouponOrderDetail();
            orderDetail.setInservice(true);
            orderDetail.setOrgId(orgId);
            orderDetail.setType(5);
            orderDetail.setCouponId(couponId);
            orderDetail.setCouponNumber(coupon.getCouponCode());
            orderDetail.setCouponName(detailModel.getCouponName());
            orderDetail.setPrice(price);
            orderDetail.setQuantity(quantity);
            orderDetail.setReceivableAmount(coupon.getSoldAmount().multiply(BigDecimal.valueOf(quantity)));
            totalReceivable = totalReceivable.add(orderDetail.getReceivableAmount());
            orderDetail.setConsulterId(detailModel.getConsulterId());
            orderDetail.setExecutorId(detailModel.getExecutorId());
            orderDetail.setSaleChannelId(name.getId());
            orderDetail.setRemarks(detailModel.getRemark());
            orderDetail.setCrtId(userId);
            orderDetail.setCrtTime(date);
            orderDetail.setUpdId(userId);
            orderDetail.setUpdTime(date);
            list.add(orderDetail);
        }
        couponOrder.setTotalAmount(totalPrice);
        couponOrder.setReceivableAmount(totalReceivable);
        couponOrder.setCrtId(Objects.isNull(couponOrder.getCrtId()) ? userId : couponOrder.getCrtId());
        couponOrder.setUpdId(userId);
        couponOrder.setUpdTime(date);
        couponOrder.setCrtTime(Objects.isNull(couponOrder.getCrtTime()) ? date : couponOrder.getCrtTime());
        return list;
    }

    private synchronized String generateOrderRecordNumber(Integer orgId) {
        String number = couponOrderMapper.selectOrderNumberByOrgId(orgId, new Date(System.currentTimeMillis()));
        String suffix = String.format("%04d", Integer.parseInt(number) + 1);
        return String.format(
                "DD%s%s%s", String.format("%04d", orgId), new DateTime().toString("yyMMdd"), suffix);
    }

    private Map<Integer, CouponCommonInfo> listCoupon(Collection<Integer> ids) {
        Example example = new Example(CouponCommonInfo.class);
        example.createCriteria().andIn("id", ids);
        List<CouponCommonInfo> coupons = couponMapper.selectByExample(example);
        return coupons.stream().collect(toMap(CouponCommonInfo::getId, Function.identity()));
    }

    public CouponOrderVO detail(Integer orderId) {
        CouponOrderVO couponOrderVO = new CouponOrderVO();
        CouponOrder order = getOrder(orderId);
        if (Objects.isNull(order)) {
            return couponOrderVO;
        }
        couponOrderVO.setOrderId(orderId);
        List<CouponOrderDetail> details = listOrderDetail(orderId, null);
        List<Integer> couponIds = details.stream().map(CouponOrderDetail::getCouponId).collect(toList());
        Map<Integer, CouponCommonInfo> collect = listCoupon(couponIds);
        List<CouponOrderDetailVO> detailVOS = Lists.newArrayList();
        CouponOrderDetailVO detailVO;
        for (CouponOrderDetail detail : details) {
            CouponCommonInfo coupon = collect.get(detail.getCouponId());
            detailVO = BeanCopierUtils.generalCopyBean(detail, CouponOrderDetailVO.class);
            Integer executorId = detail.getExecutorId();
            if (null != executorId) {
                SysUserInfoDetail executor = systemServiceFeign.findSysUserEmployeeInfoByUserId(executorId);
                detailVO.setExecutorName(null != executor ? executor.getName() : "--");
                detailVO.setExecutorId(executorId);
            }
            Integer consulterId = detail.getConsulterId();
            if (null != consulterId) {
                SysUserInfoDetail consulter = systemServiceFeign.findSysUserEmployeeInfoByUserId(consulterId);
                detailVO.setConsulterName(null != consulter ? consulter.getName() : "--");
                detail.setConsulterId(consulterId);
            }
            detailVO.setRemark(detail.getRemarks());
            detailVO.setOrderDetailId(detail.getId());
            detailVO.setCouponName(detail.getCouponName());
            detailVO.setPrice(detail.getPrice());
            detailVO.setPackagePrice(detail.getReceivableAmount());
            detailVO.setSaleAmount(coupon.getSoldAmount());
            detailVO.setChannelName(salesChannelBiz.selectById(detail.getSaleChannelId()).getName());
            detailVOS.add(detailVO);
        }
        CouponBillPay billPay = listPay(orderId);
        if (Objects.nonNull(billPay)) {
            CouponPayDetailVO payDetailVO = new CouponPayDetailVO();
            payDetailVO.setOrgId(billPay.getOrgId());
            payDetailVO.setOrgName(systemServiceFeign.findOrgInfoByOrgId(billPay.getOrgId()).getAbbreviation());
            payDetailVO.setReceivedAmount(billPay.getReceivedAmount());
            payDetailVO.setPayDate(DateUtil.format(billPay.getCrtTime()));
            couponOrderVO.setPayDetail(payDetailVO);
        }
        couponOrderVO.setDetail(detailVOS);
        couponOrderVO.setReceivableAmount(detailVOS.stream().map(CouponOrderDetailVO::getReceivableAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        return couponOrderVO;
    }

    public CouponOrder getOrder(Integer orderId) {
        Example example = new Example(CouponOrder.class);
        example.createCriteria().andEqualTo("id", orderId)
                .andEqualTo("inservice", true);
        return couponOrderMapper.selectOneByExample(example);
    }

    List<CouponOrderDetail> listOrderDetail(Integer orderId, Integer couponId) {
        Example example = new Example(CouponOrderDetail.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo("orderId", orderId)
                .andEqualTo("inservice", true);
        if (Objects.nonNull(couponId)) {
            criteria.andEqualTo("couponId", couponId);
        }
        return orderDetailMapper.selectByExample(example);
    }

    private CouponBillPay listPay(Integer orderId) {
        Example example = new Example(CouponBillPay.class);
        example.createCriteria().andEqualTo("orderId", orderId)
                .andEqualTo("inservice", true);
        return billPayMapper.selectOneByExample(example);
    }

    private List<Card> selectCard(Integer patientId, List<Card> cards, List<CouponOrderDetailModel> detail, LocalDateTime date) {
        Map<Integer, Integer> collect2 = detail.stream().collect(toMap(CouponOrderDetailModel::getCouponId, CouponOrderDetailModel::getQuantity));
        Map<Integer, List<Card>> collect = cards.stream()
                .collect(groupingBy(Card::getCouponId
                        , collectingAndThen(toList(), list -> list.stream()
                                .sorted(Comparator.comparingInt(Card::getId))
                                .limit(collect2.get(list.get(0).getCouponId())).collect(toList()))));
        SalesChannel name = salesChannelBiz.getByName("艾维门诊");
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        List<Card> list1 = Lists.newArrayList();
        String mobile = patientFeign.findPatientInfoByIds(Lists.newArrayList(patientId)).get(0).getMobile();
        for (Map.Entry<Integer, List<Card>> entry : collect.entrySet()) {
            List<Card> list = collect.get(entry.getKey());
            list.forEach(card -> {
                card.setStatus(1);
                card.setSoldTarget(patientId.toString());
                card.setSoldPhoneNumber(mobile);
                card.setSoldType(0);
                card.setSendText(0);
                card.setSoldAndPay(1);
                card.setSaleChannelId(name.getId());
                card.setSoldWay(1);
                card.setPay(1);
                card.setSoldDate(date);
                card.setPayDate(date);
                card.setSellerUserId(userId);
                card.setUpdId(userId);
                card.setUpdTime(date);
                card.setBuyerId(patientId);
                mqServiceFeign.sendMessage(card.getId(), UPDATE, BaseCardSingle);
                log.info("【售卖划扣卡券发送消息成功】：卡券id[{}]", card.getId());
            });
            list1.addAll(list);
        }
        cardMapper.soldList(list1);
        return list1;
    }

    private void checkRemaining(List<Card> cards, List<CouponOrderDetailModel> detail, Map<Integer, CouponCommonInfo> collect) {
        Map<Integer, Long> unsold = cards.stream().collect(groupingBy(Card::getCouponId, counting()));
        for (CouponOrderDetailModel detailModel : detail) {
            Integer couponId = detailModel.getCouponId();
            Integer quantity = detailModel.getQuantity();
            CouponCommonInfo coupon = collect.get(couponId);
            Long remaining = unsold.getOrDefault(couponId, 0L);
            if (quantity - remaining > 0) {
                throw ClientServiceException.wrap(COUPON_STOCK_LACK, coupon.getName());
            }
        }
    }

    private List<CouponOrderVirtual> orderVirtual(CouponOrder couponOrder, List<Card> list
            , LocalDateTime now, Map<Integer, CouponCommonInfo> collect,Map<Integer, List<DeductionItemPeriod>> periodsCollect) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        CouponOrderVirtual orderVirtual;
        List<CouponOrderVirtual> list1 = Lists.newArrayList();
        Date date = DateUtil.localDateTimeToDate(now);
        for (Card card : list) {
            orderVirtual = new CouponOrderVirtual();
            orderVirtual.setOrderId(couponOrder.getId());
            orderVirtual.setOrderSn(couponOrder.getOrderRecordNum());
            orderVirtual.setCardId(card.getId());
            orderVirtual.setCouponId(card.getCouponId());
            orderVirtual.setPatientId(couponOrder.getPatientId());
            orderVirtual.setCardNumber(card.getCardNumber());
            orderVirtual.setSoldDate(date);
            orderVirtual.setCrtId(userId);
            orderVirtual.setUpdId(userId);
            orderVirtual.setUpdTime(date);
            orderVirtual.setCrtTime(date);
            CouponCommonInfo couponCommonInfo = collect.get(card.getCouponId());
            orderVirtual.setPackageUnitPrice(couponCommonInfo.getSoldAmount());
            BigDecimal price = periodsCollect.get(orderVirtual.getCouponId()).stream().map(DeductionItemPeriod::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            orderVirtual.setPrice(price);
            orderVirtual.setCouponName(couponCommonInfo.getName());
            list1.add(orderVirtual);
        }
        return list1;
    }

    public void revoke(List<Card> list) {
        for (Card card : list) {
            card.setStatus(0);
            card.setPatientId(null);
            card.setSoldType(null);
            card.setSendText(null);
            card.setSoldAndPay(null);
            card.setSaleChannelId(null);
            card.setSoldWay(null);
            card.setPay(null);
            card.setSoldDate(null);
            card.setPayDate(null);
            card.setSellerUserId(null);
            card.setBuyerId(null);
            card.setActiveOrgId(null);
            card.setActiveUserId(null);
            card.setSoldTarget(null);
            card.setSoldPhoneNumber(null);
            card.setActiveDate(null);
            cardMapper.updateByPrimaryKey(card);
            mqServiceFeign.sendMessage(card.getId(), UPDATE, BaseCardSingle);
            log.info("【取消售卖划扣卡券发送消息成功】：卡券id[{}]", card.getId());
        }
    }

    public Integer click(Integer patientId) {
        int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        List<CouponOrder> orders = existChargeOrder(patientId, orgId);
        return CollectionUtils.isNotEmpty(orders) ? orders.get(0).getId() : null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer orderId) {
        CouponOrder couponOrder = getOrder(orderId);
        if (Objects.nonNull(couponOrder)) {
            deleteVirtual(orderId);
            removeDetail(orderId);
            couponOrder.setInservice(false);
            couponOrderMapper.updateByPrimaryKeySelective(couponOrder);
            mqServiceFeign.sendMessage(couponOrder.getId(), 2, BaseCouponBill);
        }
    }

    private List<CouponOrder> existChargeOrder(Integer patientId, Integer orgId) {
        Example example = new Example(CouponOrder.class);
        example.createCriteria().andEqualTo("patientId", patientId)
                .andEqualTo("orgId", orgId)
                .andEqualTo("status", 0)
                .andEqualTo("inservice", true);
        return couponOrderMapper.selectByExample(example);
    }

    public List<CouponOrderVirtual> listOrderVirtual(Integer orderId, Integer cardId, boolean all) {
        Example example = new Example(CouponOrderVirtual.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo("orderId", orderId);
        if (Objects.nonNull(cardId)) {
            criteria.andEqualTo("cardId", cardId);
        }
        if (!all) {
            criteria.andEqualTo("inservice", true);
        }
        return virtualMapper.selectByExample(example);
    }


    public CouponOrderVirtual getOrderVirtual(Integer cardId) {
        Example example = new Example(CouponOrderVirtual.class);
        example.createCriteria().andEqualTo("cardId", cardId)
                .andEqualTo("inservice", true);
        return virtualMapper.selectOneByExample(example);
    }

    private List<Card> listCard(Collection<Integer> cardIds) {
        Example example = new Example(Card.class);
        example.createCriteria().andIn("id", cardIds);
        return cardMapper.selectByExample(example);
    }

    private void deleteVirtual(Integer orderId) {
        List<CouponOrderVirtual> virtuals = deleteVirtuals(orderId);
        List<Integer> cardIds = virtuals.stream().map(CouponOrderVirtual::getCardId).collect(toList());
        if (CollectionUtils.isNotEmpty(cardIds)) {
            List<Card> cards = listCard(cardIds);
            revoke(cards);
        }
    }

    public void refundVirtual(List<CouponOrderDetail> details, CouponOrderDetail detail
            , List<CouponOrderVirtual> virtuals, CouponOrderVirtual virtual, CouponOrder order) {
        virtual.setInservice(false);
        virtualMapper.updateByPrimaryKeySelective(virtual);
        int totalQuantity = details.stream().map(CouponOrderDetail::getQuantity).reduce(0, Integer::sum);
        int couponCount = detail.getQuantity();
        long refundCount = virtuals.stream().filter(t -> !t.getInservice() && Objects.equals(t.getCouponId(), detail.getCouponId()))
                .count();
        long count = virtuals.stream().filter(t -> !t.getInservice()).count();
        if (refundCount >= couponCount) {
            detail.setInservice(false);
            orderDetailMapper.updateByPrimaryKeySelective(detail);
        }
        if (totalQuantity <= count) {
            order.setInservice(false);
            couponOrderMapper.updateByPrimaryKeySelective(order);
        }
    }

    private void removeDetail(Integer orderId) {
        List<CouponOrderDetail> details = listOrderDetail(orderId, null);
        details.forEach(t -> {
            t.setInservice(false);
            orderDetailMapper.updateByPrimaryKeySelective(t);
        });
    }

    private void deleteDetail(Integer orderId) {
        List<CouponOrderDetail> details = listOrderDetail(orderId, null);
        details.forEach(t -> {
            orderDetailMapper.deleteByPrimaryKey(t.getId());
        });
    }

    private List<CouponOrderVirtual> deleteVirtuals(Integer orderId) {
        List<CouponOrderVirtual> virtuals = listOrderVirtual(orderId, null, false);
        virtuals.forEach(t -> {
            virtualMapper.deleteByPrimaryKey(t.getId());
        });
        return virtuals;
    }

    public void updateOrder(Integer orderId, BigDecimal totalCharge) {
        CouponOrder order = getOrder(orderId);
        if (Objects.nonNull(order) && Objects.equals(0, order.getStatus())) {
            log.info("订单已收费:{}", orderId);
            order.setStatus(1);
            order.setReceivedAmount(totalCharge);
            couponOrderMapper.updateByPrimaryKeySelective(order);
        }
    }
}
