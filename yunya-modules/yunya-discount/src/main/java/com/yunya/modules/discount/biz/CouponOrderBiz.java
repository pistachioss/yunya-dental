package com.yunya.modules.discount.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.discount.domain.model.CouponOrderDetailModel;
import com.yunya.feign.discount.domain.model.CouponOrderModel;
import com.yunya.feign.discount.domain.vo.CouponOrderDetailVO;
import com.yunya.feign.discount.domain.vo.CouponOrderVO;
import com.yunya.feign.discount.domain.vo.CouponPayDetailVO;
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
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

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
    private CardBiz cardBiz;
    @Resource
    private CouponCommonInfoBiz couponBiz;
    @Resource
    private DeductionPeriodBiz periodBiz;
    @Resource
    private SalesChannelBiz salesChannelBiz;
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;

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
            List<CouponOrderDetail> build = build(model, couponIds, collect, couponOrder, now);
            couponOrderMapper.insert(couponOrder);
            build.forEach(o -> o.setOrderId(couponOrder.getId()));
            orderDetailMapper.insertList(build);
            List<CouponOrderVirtual> virtuals = orderVirtual(couponOrder, list, now);
            virtualMapper.insertList(virtuals);
            vo = detail(couponOrder.getId());
        } catch (Exception e) {
            if (CollectionUtils.isNotEmpty(list)) {
                revoke(list);
            }
        }
        return vo;
    }

    public void occur(CouponBillPay billPay, Integer occurType, Date date) {
        int id = Integer.parseInt(BaseContextHandler.getUserID());
        if (Objects.nonNull(billPay)) {
            List<CouponOrderDetail> couponOrderDetails = listOrderDetail(billPay.getOrderId());
            List<CouponChangeRecord> list = couponOrderDetails.stream().map(t -> {
                CouponChangeRecord couponChangeRecord = new CouponChangeRecord();
                couponChangeRecord.setOrgId(billPay.getOrgId());
                couponChangeRecord.setPatientId(billPay.getPatientId());
                couponChangeRecord.setCouponBillId(billPay.getBillId());
                couponChangeRecord.setCouponId(t.getCouponId());
                couponChangeRecord.setOccurType(occurType);
                couponChangeRecord.setOccurAmount(t.getReceivableAmount());
                couponChangeRecord.setOperatorUserId(id);
                couponChangeRecord.setOccurDate(date);
                return couponChangeRecord;
            }).collect(toList());
            list.forEach(t -> changeRecordMapper.insertSelective(t));
        }
    }

    private List<CouponOrderDetail> build(CouponOrderModel model, List<Integer> couponIds, Map<Integer, CouponCommonInfo> collect1
            , CouponOrder couponOrder, LocalDateTime now) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        Integer patientId = model.getPatientId();
        List<CouponOrderDetailModel> detail = model.getDetail();
        List<DeductionItemPeriod> periods = periodBiz.listByCoupon(couponIds, DateUtil.localDateTimeToDate(now));
        SalesChannel name = salesChannelBiz.getByName("艾维门诊");
        if (Objects.isNull(name)) {
            throw ClientServiceException.wrap(SALE_CHANNEL_NULL);
        }
        Map<Integer, List<DeductionItemPeriod>> collect = periods.stream().collect(groupingBy(DeductionItemPeriod::getCouponId, toList()));
        couponOrder.setOrgId(orgId);
        couponOrder.setPatientId(patientId);
        couponOrder.setOrderRecordNum(generateOrderRecordNumber(orgId));
        couponOrder.setStatus(0);
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalReceivable = BigDecimal.ZERO;
        CouponOrderDetail orderDetail;
        List<CouponOrderDetail> list = Lists.newArrayList();
        for (CouponOrderDetailModel detailModel : detail) {
            Integer couponId = detailModel.getCouponId();
            Integer quantity = detailModel.getQuantity();
            List<DeductionItemPeriod> period = collect.get(couponId);
            CouponCommonInfo coupon = collect1.get(couponId);
            BigDecimal price = period.stream().map(p -> p.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalPrice = totalPrice.add(price);
            orderDetail = new CouponOrderDetail();
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
            list.add(orderDetail);
        }
        couponOrder.setTotalAmount(totalPrice);
        couponOrder.setReceivableAmount(totalReceivable);
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
        List<CouponOrderDetail> details = listOrderDetail(orderId);
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
            }
            Integer consulterId = detail.getConsulterId();
            if (null != consulterId) {
                SysUserInfoDetail consulter = systemServiceFeign.findSysUserEmployeeInfoByUserId(consulterId);
                detailVO.setConsulterName(null != consulter ? consulter.getName() : "--");
            }
            detailVO.setOrderDetailId(detail.getId());
            detailVO.setCouponName(detail.getCouponName());
            detailVO.setPrice(detail.getPrice());
            detailVO.setPackagePrice(detail.getReceivableAmount());
            detailVO.setSaleAmount(coupon.getSoldAmount());
            detailVO.setChannelName(salesChannelBiz.selectById(detail.getSaleChannelId()).getName());
            detailVOS.add(detailVO);
        }
        CouponBillPay billPay = listPay(orderId);
        CouponPayDetailVO payDetailVO = new CouponPayDetailVO();
        payDetailVO.setOrgId(billPay.getOrgId());
        payDetailVO.setOrgName(systemServiceFeign.findOrgInfoByOrgId(billPay.getOrgId()).getAbbreviation());
        payDetailVO.setReceivedAmount(billPay.getReceivedAmount());
        payDetailVO.setPayDate(DateUtil.format(billPay.getCrtTime()));
        couponOrderVO.setPayDetail(payDetailVO);
        couponOrderVO.setDetail(detailVOS);
        couponOrderVO.setReceivableAmount(detailVOS.stream().map(CouponOrderDetailVO::getReceivableAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        return couponOrderVO;
    }

    private CouponOrder getOrder(Integer orderId) {
        Example example = new Example(CouponOrder.class);
        example.createCriteria().andEqualTo("id", orderId)
                .andEqualTo("inservice", true);
        return couponOrderMapper.selectOneByExample(example);
    }

    private List<CouponOrderDetail> listOrderDetail(Integer orderId) {
        Example example = new Example(CouponOrderDetail.class);
        example.createCriteria().andEqualTo("orderRecordId", orderId)
                .andEqualTo("inservice", true);
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
        for (Map.Entry<Integer, List<Card>> entry : collect.entrySet()) {
            List<Card> list = collect.get(entry.getKey());
            list.forEach(card -> {
                card.setStatus(1);
                card.setSoldTarget(patientId.toString());
                card.setSoldPhoneNumber(patientId.toString());
                card.setPatientId(patientId);
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
            , LocalDateTime now) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        CouponOrderVirtual orderVirtual;
        List<CouponOrderVirtual> list1 = Lists.newArrayList();
        for (Card card : list) {
            orderVirtual = new CouponOrderVirtual();
            orderVirtual.setOrderId(couponOrder.getId());
            orderVirtual.setOrderSn(couponOrder.getOrderRecordNum());
            orderVirtual.setCardId(card.getId());
            orderVirtual.setCouponId(card.getCouponId());
            orderVirtual.setPatientId(couponOrder.getPatientId());
            orderVirtual.setCardNumber(card.getCardNumber());
            orderVirtual.setSoldDate(DateUtil.localDateTimeToDate(now));
            orderVirtual.setCrtId(userId);
            orderVirtual.setUpdId(userId);
            list1.add(orderVirtual);
        }
        return list1;
    }

    private void revoke(List<Card> list) {
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
            cardMapper.updateByPrimaryKey(card);
        }
    }

    public Integer click(Integer patientId) {
        int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        CouponOrder order = existChargeOrder(patientId, orgId);
        return Objects.nonNull(order) ? order.getId() : null;
    }

    public void delete(Integer orderId) {
        CouponOrder couponOrder = getOrder(orderId);
        if (Objects.nonNull(couponOrder)) {
            deleteVirtual(orderId);
            List<CouponOrderDetail> details = listOrderDetail(orderId);
            details.forEach(t -> {
                t.setInservice(false);
                orderDetailMapper.updateByPrimaryKeySelective(t);
            });
            couponOrder.setInservice(false);
            couponOrderMapper.updateByPrimaryKeySelective(couponOrder);
        }
    }

    private CouponOrder existChargeOrder(Integer patientId, Integer orgId) {
        Example example = new Example(CouponOrder.class);
        example.createCriteria().andEqualTo("patientId", patientId)
                .andEqualTo("orgId", orgId)
                .andEqualTo("status", 0)
                .andEqualTo("inservice", true);
        return couponOrderMapper.selectOneByExample(example);
    }

    private List<CouponOrderVirtual> getOrderVirtual(Integer orderId) {
        Example example = new Example(CouponOrderVirtual.class);
        example.createCriteria().andEqualTo("orderId", orderId)
                .andEqualTo("inservice", true);
        return virtualMapper.selectByExample(example);
    }

    private List<Card> listCard(Collection<Integer> cardIds) {
        Example example = new Example(Card.class);
        example.createCriteria().andIn("id", cardIds)
                .andGreaterThan("status", 2)
                .andEqualTo("inservice", true);
        return cardMapper.selectByExample(example);
    }

    private void deleteVirtual(Integer orderId) {
        List<CouponOrderVirtual> virtuals = getOrderVirtual(orderId);
        List<Integer> cardIds = virtuals.stream().map(CouponOrderVirtual::getCardId).collect(toList());
        List<Card> cards = listCard(cardIds);
        if (CollectionUtils.isNotEmpty(cards)) {
            revoke(cards);
        }
    }

    private void updateDetail(Integer orderId) {
        Example example = new Example(CouponOrderDetail.class);
        example.createCriteria().andEqualTo("orderId", orderId);
        CouponOrderDetail detail = new CouponOrderDetail();
        detail.setInservice(false);
        orderDetailMapper.updateByExampleSelective(detail, example);
    }
}
