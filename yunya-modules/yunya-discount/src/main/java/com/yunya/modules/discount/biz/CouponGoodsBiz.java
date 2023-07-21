package com.yunya.modules.discount.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.discount.domain.vo.CouponGoodsVO;
import com.yunya.feign.discount.domain.vo.DeductionCategoryVO;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.discount.DeductionItemPeriod;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import com.yunya.modules.discount.mapper.DeductionItemPeriodMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

/**
 * @auther: xy
 * @date: 2023/6/27
 */
@Service
@Slf4j
public class CouponGoodsBiz {
    @Resource
    private DeductionItemPeriodMapper periodMapper;
    @Resource
    private CouponCommonInfoMapper couponMapper;

    public List<CouponGoodsVO> hkList(Integer categoryId) {
        int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        List<CouponGoodsVO> coupons = couponMapper.listCouponGoods(null, categoryId);
        if (CollectionUtils.isEmpty(coupons)) {
            return Lists.newArrayList();
        }
        Map<Integer, CouponGoodsVO> collect = coupons.stream()
                .collect(toMap(CouponGoodsVO::getId, Function.identity()));
        Set<Integer> couponIds = collect.keySet();
        List<DeductionItemPeriod> deductionItemPeriods = periodMapper.selectItem(couponIds, new Date());
        Map<Integer, BigDecimal> collect1 = deductionItemPeriods.stream()
                .collect(groupingBy(DeductionItemPeriod::getCouponId, collectingAndThen(toList()
                        , list -> list.stream().map(DeductionItemPeriod::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add))));
        coupons.forEach(t -> t.setPrice(collect1.get(t.getId())));
//        List<Card> cards = cardMapper.listRemaining(couponIds, orgId);
//        if (CollectionUtils.isEmpty(cards)) {
//            log.info("该门诊没有剩余卡券:{}", orgId);
//            return Lists.newArrayList();
//        }
//        List<Integer> collect1 = cards.stream().map(Card::getCouponId)
//                .collect(toList());
//        coupons.removeIf(t -> !collect1.contains(t.getId()));
        return coupons;
    }

    public Set<DeductionCategoryVO> category() {
        List<CouponGoodsVO> goodsVOS = hkList(null);
        return goodsVOS.stream().map(t -> {
            DeductionCategoryVO vo = new DeductionCategoryVO();
            vo.setId(t.getCategoryId());
            vo.setName(t.getCategoryName());
            return vo;
        }).collect(toSet());
    }

}
