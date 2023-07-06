package com.yunya.modules.discount.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.discount.domain.bo.CouponRemainingBo;
import com.yunya.feign.discount.domain.model.CouponOrderDetailModel;
import com.yunya.feign.discount.domain.model.CouponOrderModel;
import com.yunya.feign.discount.domain.vo.CouponOrderDetailVO;
import com.yunya.feign.discount.domain.vo.CouponOrderVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.models.discount.*;
import com.yunya.modules.discount.mapper.CardMapper;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import com.yunya.modules.discount.mapper.CouponOrderDetailMapper;
import com.yunya.modules.discount.mapper.CouponOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
@Transactional(rollbackFor = Exception.class)
public class CouponOrderBiz {

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
    @Resource
    private CardMapper cardMapper;
    @Resource
    private CouponCommonInfoMapper couponMapper;
    @Resource
    private CouponOrderMapper couponOrderMapper;
    @Resource
    private CouponOrderDetailMapper orderDetailMapper;

    public CouponOrderVO soldCard(CouponOrderModel model) {
        log.info("划扣下单参数：{}", model);
        int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        List<CouponOrderDetailModel> detail = model.getDetail();
        List<Integer> couponIds = detail.stream().map(CouponOrderDetailModel::getCouponId).collect(toList());
        List<CouponRemainingBo> cards = cardMapper.listRemaining(couponIds, orgId);
        Map<Integer, Long> unsold = cards.stream().collect(groupingBy(CouponRemainingBo::getCouponId, counting()));
        Map<Integer, CouponCommonInfo> collect = listCoupon(couponIds);
        for (CouponOrderDetailModel detailModel : detail) {
            Integer couponId = detailModel.getCouponId();
            Integer quantity = detailModel.getQuantity();
            CouponCommonInfo coupon = collect.get(couponId);
            Long remaining = unsold.getOrDefault(couponId, 0L);
            if (quantity - remaining > 0 ) {
                throw ClientServiceException.wrap(COUPON_STOCK_LACK, coupon.getName());
            }
        }
        CouponOrder couponOrder = new CouponOrder();
        List<CouponOrderDetail> build = build(model, couponIds, collect, couponOrder);
        couponOrderMapper.insert(couponOrder);
        build.forEach(o -> o.setOrderRecordId(couponOrder.getId()));
        orderDetailMapper.insertList(build);
        return detail(couponOrder.getId());
    }

    private List<CouponOrderDetail> build(CouponOrderModel model, List<Integer> couponIds, Map<Integer, CouponCommonInfo> collect1
            , CouponOrder couponOrder) {
        Date now = new Date();
        int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        Integer patientId = model.getPatientId();
        List<CouponOrderDetailModel> detail = model.getDetail();
        List<DeductionItemPeriod> periods = periodBiz.listByCoupon(couponIds, now);
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
//            orderDetail.setCardId();
            orderDetail.setCouponId(couponId);
            orderDetail.setCouponName(detailModel.getCouponName());
            orderDetail.setPrice(price);
            orderDetail.setQuantity(quantity);
            orderDetail.setReceivableAmount(coupon.getSoldAmount().multiply(BigDecimal.valueOf(quantity)));
            totalReceivable = totalReceivable.add(orderDetail.getReceivableAmount());
            orderDetail.setConsulterId(detailModel.getConsulterId());
            orderDetail.setExecutorId(detailModel.getExecutorId());
            orderDetail.setSaleChannelId(name.getId());
            orderDetail.setRemarks(detailModel.getRemark());
            orderDetail.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
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
        Map<Integer, CouponCommonInfo> collect  = listCoupon(couponIds);
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
}
